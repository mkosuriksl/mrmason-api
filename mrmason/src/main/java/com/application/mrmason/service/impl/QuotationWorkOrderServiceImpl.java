package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.QuotationFullResponseDTO;
import com.application.mrmason.dto.QuotationWorkOrderRequestDTO;
import com.application.mrmason.dto.QuotationWorkOrderResponseDTO;
import com.application.mrmason.entity.QuotationWorkOrder;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.ServiceRequestQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.QuotationWorkOrderRepository;
import com.application.mrmason.repository.ServiceRequestPaintQuotationRepository;
import com.application.mrmason.repository.ServiceRequestQuotationRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.QuotationWorkOrderService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;

@Service
public class QuotationWorkOrderServiceImpl implements QuotationWorkOrderService {
	@Autowired
	private QuotationWorkOrderRepository repository;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ServiceRequestQuotationRepository quotationRepository;

	@Autowired
	private ServiceRequestPaintQuotationRepository paintQuotationRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<QuotationWorkOrderResponseDTO> create(List<QuotationWorkOrderRequestDTO> requestDTOList,
			RegSource regSource) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInSPInfo(regSource);
		if (!UserType.Developer.name().equals(userInfo.role)) {
			throw new AccessDeniedException("Only Developer users can access this API.");
		}

		List<QuotationWorkOrderResponseDTO> responseList = new ArrayList<>();

		for (QuotationWorkOrderRequestDTO dto : requestDTOList) {
			ServiceRequestQuotation quotation = quotationRepository.findById(dto.getQuotationId())
					.orElseThrow(() -> new IllegalArgumentException("Quotation ID not found: " + dto.getQuotationId()));

			if (!SPWAStatus.APPROVED.equals(quotation.getStatus())) {
				throw new AccessDeniedException("Quotation ID " + dto.getQuotationId() + " is not APPROVED.");
			}

			QuotationWorkOrder entity = new QuotationWorkOrder();
			entity.setUpdatedBy(userInfo.userId);
			entity.setUpdatedDate(new Date());
			entity.setSpId(userInfo.userId);
			entity.setWoGenerateDate(new Date());
			BeanUtils.copyProperties(dto, entity);

			QuotationWorkOrder saved = repository.save(entity);
			QuotationWorkOrderResponseDTO responseDTO = convertToResponseDTO(saved);
			responseList.add(responseDTO);
		}

		return responseList;
	}

	private QuotationWorkOrderResponseDTO convertToResponseDTO(QuotationWorkOrder entity) {
		QuotationWorkOrderResponseDTO dto = new QuotationWorkOrderResponseDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
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
	public List<QuotationWorkOrderResponseDTO> update(List<QuotationWorkOrderRequestDTO> requestDTOList,
			RegSource regSource) throws AccessDeniedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuotationFullResponseDTO> getWorkOrderDetails(String quotationWorkOrder, String quotationId,
			Date fromDate, Date toDate) {
		List<QuotationFullResponseDTO> result = new ArrayList<>();

		List<QuotationWorkOrder> workOrders = repository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (quotationWorkOrder != null && !quotationWorkOrder.isBlank()) {
				predicates.add(cb.equal(root.get("quotationWorkOrder"), quotationWorkOrder));
			}

			if (quotationId != null && !quotationId.isBlank()) {
				predicates.add(cb.equal(root.get("quotationId"), quotationId));
			}

			if (fromDate != null && toDate != null) {
				predicates.add(cb.between(root.get("woGenerateDate"), new java.sql.Date(fromDate.getTime()),
						new java.sql.Date(toDate.getTime())));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		});

		for (QuotationWorkOrder workOrder : workOrders) {
			QuotationFullResponseDTO dto = new QuotationFullResponseDTO();
			dto.setWorkOrder(modelMapper.map(workOrder, QuotationWorkOrderResponseDTO.class));

			ServiceRequestQuotation quotation = quotationRepository.findById(workOrder.getQuotationId()).orElse(null);

			if (quotation != null) {
				dto.setHeaderQuotation(quotation);
				List<ServiceRequestPaintQuotation> paintList = paintQuotationRepository
						.findByRequestId(quotation.getRequestId());
				dto.setPaintQuotations(paintList);
			}

			result.add(dto);
		}

		return result;
	}

	@Override
	public Page<QuotationWorkOrder> getPayment(String requestLineId, String taskName, Integer amount,
			Integer workPersentage, Integer amountPersentage, String dailylaborPay, String advancedPayment,
			RegSource regSource, Pageable pageable) throws AccessDeniedException {
		// TODO Auto-generated method stub
		return null;
	}

}
