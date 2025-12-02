package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.SparePartDto;
import com.application.mrmason.dto.SparePartEntity;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.SparePartRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SparePartService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SparePartServiceImpl implements SparePartService {

	@Autowired
	private SparePartRepository sparePartRepo;

	@Autowired
	CustomerRegistrationRepo regiRepo;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	public AdminDetailsRepo adminRepo;
	@Autowired
	private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;
	@Autowired
	UserDAO userDAO;

	@Override
	public SparePartDto addSparePart(SparePartDto dto, RegSource regSource) {

	    // 🔥 Step 1: Generate composite primary key manually
	    String compositeKey = dto.getRequestId() + "_" + dto.getSkuId();

	    // 🔥 Step 2: Check duplicate
	    if (sparePartRepo.existsByRequestIdSkuId(compositeKey)) {
	        throw new RuntimeException(
	            "Duplicate SparePart entry: RequestId '" + dto.getRequestId() +
	            "' with SkuId '" + dto.getSkuId() + "' already exists.");
	    }

	    SparePartEntity e = new SparePartEntity();
	    BeanUtils.copyProperties(dto, e);

	    // Set primary key manually
	    e.setRequestIdSkuId(compositeKey);

	    e.setUpdatedBy(dto.getUserId());
	    e.setUpdatedDate(new Date());

	    SparePartEntity saved = sparePartRepo.save(e);

	    SparePartDto response = new SparePartDto();
	    BeanUtils.copyProperties(saved, response);

	    return response;
	}


	@Override
	public SparePartDto updateSparePart(SparePartDto dto, RegSource regSource) {

		// Fetch existing entity
		SparePartEntity entity = sparePartRepo.findById(dto.getRequestIdSkuId()).orElseThrow(
				() -> new ResourceNotFoundException("Spare part not found with id: " + dto.getRequestIdSkuId()));


		// Update fields from DTO
		entity.setSparePart(dto.getSparePart());
		entity.setUserId(dto.getUserId());
		entity.setBrand(dto.getBrand());
		entity.setModel(dto.getModel());
		entity.setAmount(dto.getAmount());
		entity.setDiscount(dto.getDiscount());
		entity.setGst(dto.getGst());
		entity.setWarranty(dto.getWarranty());
		entity.setUpdatedBy(dto.getUserId());
		entity.setUpdatedDate(new Date());
		entity.setTotalAmount(dto.getTotalAmount());

		// Save updated entity
		SparePartEntity saved = sparePartRepo.save(entity);

		// Convert to DTO
		SparePartDto response = new SparePartDto();
		BeanUtils.copyProperties(saved, response);

		return response;
	}

	@Override
	public Page<SparePartEntity> getSparePart(String requestId, String sparePart, String brand, String model,
			String userId, Pageable pageable) throws AccessDeniedException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<SparePartEntity> query = cb.createQuery(SparePartEntity.class);
		Root<SparePartEntity> root = query.from(SparePartEntity.class);
		List<Predicate> predicates = new ArrayList<>();

		if (requestId != null && !requestId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("requestId"), requestId));
		}
		if (sparePart != null && !sparePart.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("sparePart"), sparePart));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (model != null && !model.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("model"), model));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<SparePartEntity> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<SparePartEntity> countRoot = countQuery.from(SparePartEntity.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (requestId != null && !requestId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("requestId"), requestId));
		}
		if (sparePart != null && !sparePart.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("sparePart"), sparePart));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		}
		if (model != null && !model.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("model"), model));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	private static class UserInfo {

		String userId;

		UserInfo(String userId) {
			this.userId = userId;
		}

	}

	private UserInfo getLoggedInUserInfo(RegSource regSource, UserType userType) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.isEmpty()) {
			throw new ResourceNotFoundException("No roles assigned for user: " + loggedInUserEmail);
		}

//		userType = UserType.valueOf(roleNames.get(0));
		String userId;

		switch (userType) {
		case Adm:
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getEmail();
			break;

		case MS:
			MaterialSupplierQuotationUser msUser = materialSupplierQuotationUserDAO
					.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("Material User not found: " + loggedInUserEmail));
			userId = msUser.getBodSeqNo();
			break;

		case EC:
		case Developer:
			// Try CustomerRegistration first
			CustomerRegistration customer = regiRepo
					.findByUserEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource).orElse(null);

			if (customer != null) {
				userId = customer.getUserid();
			} else {
				// If not found in Customer, try User table
				User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
						.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
				userId = user.getBodSeqNo();
			}
			break;

		default:
			throw new ResourceNotFoundException("Unsupported user type: " + userType);
		}

		return new UserInfo(userId);
	}
}
