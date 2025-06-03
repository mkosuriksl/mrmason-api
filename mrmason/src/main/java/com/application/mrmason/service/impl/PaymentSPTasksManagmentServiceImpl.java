package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.PaymentSPTasksManagmentRequestDTO;
import com.application.mrmason.dto.PaymentSPTasksManagmentResponseDTO;
import com.application.mrmason.entity.AdminPopTasksManagemnt;
import com.application.mrmason.entity.PaymentSPTasksManagment;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.PaymentSPTasksManagmentRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.PaymentSPTasksManagmentService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class PaymentSPTasksManagmentServiceImpl implements PaymentSPTasksManagmentService {

	@Autowired
	private PaymentSPTasksManagmentRepository repository;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<PaymentSPTasksManagmentResponseDTO> create(List<PaymentSPTasksManagmentRequestDTO> requestDTOList,
			RegSource regSource) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInSPInfo(regSource);
		if (!UserType.Developer.name().equals(userInfo.role)) {
			throw new AccessDeniedException("Only Developer users can access this API.");
		}

		List<PaymentSPTasksManagmentResponseDTO> responseList = new ArrayList<>();

		for (PaymentSPTasksManagmentRequestDTO dto : requestDTOList) {
			PaymentSPTasksManagment entity = new PaymentSPTasksManagment();
			entity.setUpdatedBy(userInfo.userId);
			entity.setUpdatedDate(new Date());
			entity.setSpId(userInfo.userId);
			BeanUtils.copyProperties(dto, entity);

			PaymentSPTasksManagment saved = repository.save(entity);
			PaymentSPTasksManagmentResponseDTO responseDTO = convertToResponseDTO(saved);
			responseList.add(responseDTO);
		}

		return responseList;
	}

	private static class UserInfo {
		String userId;
		String role;

		UserInfo(String userId, String role) {
			this.userId = userId;
			this.role = role;
		}
	}

	private UserInfo getLoggedInSPInfo(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());
		String userId = null;
		String role = roleNames.get(0);
		UserType userType = UserType.valueOf(role);
		if (userType == UserType.Developer) {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}
		return new UserInfo(userId, role);
	}

	@Override
	public List<PaymentSPTasksManagmentResponseDTO> update(List<PaymentSPTasksManagmentRequestDTO> requestDTOList,
			RegSource regSource) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInSPInfo(regSource);
		if (!UserType.Developer.name().equals(userInfo.role)) {
			throw new AccessDeniedException("Only Developer users can access this API.");
		}

		List<PaymentSPTasksManagmentResponseDTO> responseList = new ArrayList<>();

		for (PaymentSPTasksManagmentRequestDTO requestDTO : requestDTOList) {
			Optional<PaymentSPTasksManagment> optional = repository.findByRequestLineId(requestDTO.getRequestLineId());
			if (!optional.isPresent()) {
				throw new ResourceNotFoundException(
						"PaymentSPTasksManagment not found for requestLineId: " + requestDTO.getRequestLineId());
			}

			PaymentSPTasksManagment entity = optional.get();
			entity.setUpdatedBy(userInfo.userId);
			entity.setUpdatedDate(new Date());
			BeanUtils.copyProperties(requestDTO, entity);
			PaymentSPTasksManagment updated = repository.save(entity);
			responseList.add(convertToResponseDTO(updated));
		}

		return responseList;
	}

	private PaymentSPTasksManagmentResponseDTO convertToResponseDTO(PaymentSPTasksManagment entity) {
		PaymentSPTasksManagmentResponseDTO dto = new PaymentSPTasksManagmentResponseDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	@Override
	public Page<PaymentSPTasksManagment> getPayment(String requestLineId, String taskName, Integer amount,
			Integer workPersentage, Integer amountPersentage, String dailylaborPay, String advancedPayment,
			RegSource regSource, Pageable pageable) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInSPInfo(regSource);

		if (!UserType.Developer.name().equals(userInfo.role)) {
			throw new AccessDeniedException("Only Developer users can access this API.");
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PaymentSPTasksManagment> query = cb.createQuery(PaymentSPTasksManagment.class);
		Root<PaymentSPTasksManagment> root = query.from(PaymentSPTasksManagment.class);
		List<Predicate> predicates = new ArrayList<>();

		if (requestLineId != null && !requestLineId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("requestLineId"), requestLineId));
		}
		if (taskName != null && !taskName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("taskName"), taskName));
		}
		if (amount != null) {
			predicates.add(cb.equal(root.get("amount"), amount));
		}
		if (workPersentage != null) {
			predicates.add(cb.equal(root.get("workPersentage"), workPersentage));
		}
		if (amountPersentage != null) {
			predicates.add(cb.equal(root.get("amountPersentage"), amountPersentage));
		}
		if (dailylaborPay != null && !dailylaborPay.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("dailylaborPay"), dailylaborPay));
		}
		if (advancedPayment != null && !advancedPayment.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("advancedPayment"), advancedPayment));
		}
		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<PaymentSPTasksManagment> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<PaymentSPTasksManagment> countRoot = countQuery.from(PaymentSPTasksManagment.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (requestLineId != null && !requestLineId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("requestLineId"), requestLineId));
		}
		if (taskName != null && !taskName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("taskName"), taskName));
		}
		if (amount != null) {
	        countPredicates.add(cb.equal(countRoot.get("amount"), amount));
	    }
	    if (workPersentage != null) {
	        countPredicates.add(cb.equal(countRoot.get("workPersentage"), workPersentage));
	    }
	    if (amountPersentage != null) {
	        countPredicates.add(cb.equal(countRoot.get("amountPersentage"), amountPersentage));
	    }
	    if (dailylaborPay != null && !dailylaborPay.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("dailylaborPay"), dailylaborPay));
	    }
	    if (advancedPayment != null && !advancedPayment.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("advancedPayment"), advancedPayment));
	    }
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}
}
