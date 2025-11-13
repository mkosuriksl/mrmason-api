package com.application.mrmason.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.CustomerBasicDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetWorkOrderSRHdrAndCustomerDto;
import com.application.mrmason.dto.WorkOrderCustomerResponseDto;
import com.application.mrmason.dto.WorkOrderRequest;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation2;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.ServiceRequestPaintQuotation2;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.ServiceRequestHeaderAllQuotationRepo;
import com.application.mrmason.repository.ServiceRequestHeaderAllQuotationRepo2;
import com.application.mrmason.repository.ServiceRequestPaintQuotationRepository;
import com.application.mrmason.repository.ServiceRequestPaintQuotationRepository2;
import com.application.mrmason.repository.ServiceRequestRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.ServiceRequestPaintQuotationService2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ServiceRequestPaintQuotationServiceImp2 implements ServiceRequestPaintQuotationService2 {

	@Autowired
	private ServiceRequestHeaderAllQuotationRepo serviceRequestHeaderAllQuotationRepo;

	@Autowired
	private ServiceRequestHeaderAllQuotationRepo2 serviceRequestHeaderAllQuotationRepo2;

	@Autowired
	private ServiceRequestPaintQuotationRepository serviceRequestPaintQuotationRepository;

	@Autowired
	private ServiceRequestPaintQuotationRepository2 serviceRequestPaintQuotationRepository2;
	
	@Autowired
	private ServiceRequestRepo serviceRequestRepo;
	
	@Autowired
	private CustomerRegistrationRepo customerRegistrationRepo;

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public GenericResponse<Map<String, Object>> duplicateQuotationToRepo2(WorkOrderRequest workOrderRequest, RegSource regSource) {

		UserInfo userInfo = getLoggedInUserInfo(regSource);

		// Step 1: Fetch header
		Optional<ServiceRequestHeaderAllQuotation> existingHeaderOpt = serviceRequestHeaderAllQuotationRepo
				.findById(workOrderRequest.getQuotationId());

		if (existingHeaderOpt.isEmpty()) {
			throw new ResourceNotFoundException("Quotation header not found for ID: " + workOrderRequest.getQuotationId());
		}

		ServiceRequestHeaderAllQuotation existingHeader = existingHeaderOpt.get();

		// ✅ Generate new Work Order ID
		String workOrderId = "WO" + System.currentTimeMillis();

		// Step 2: Copy header into Repo2 entity
		ServiceRequestHeaderAllQuotation2 newHeader = new ServiceRequestHeaderAllQuotation2();
//		newHeader.setQuotationId(workOrderId); // new WO id
		newHeader.setWorkOrderId(workOrderId); 
//		newHeader.setRequestId(existingHeader.getQuotationId());
		newHeader.setQuotationId(existingHeader.getQuotationId());

		newHeader.setQuotedDate(existingHeader.getQuotedDate());
		newHeader.setStatus(existingHeader.getStatus());
		newHeader.setSpId(existingHeader.getSpId());
		newHeader.setUpdatedBy(userInfo.userId);
		newHeader.setUpdatedDate(new Date());

		serviceRequestHeaderAllQuotationRepo2.save(newHeader);

		// Step 3: Fetch all quotation details
		List<ServiceRequestPaintQuotation> existingItems = serviceRequestPaintQuotationRepository
				.findByQuotationId(workOrderRequest.getQuotationId());

		if (existingItems.isEmpty()) {
			throw new ResourceNotFoundException("No quotation details found for quotationId: " + workOrderRequest.getQuotationId());
		}

		// Step 4: Clone and save into Repo2
		List<ServiceRequestPaintQuotation2> newQuotationDetails = new ArrayList<>();

		for (ServiceRequestPaintQuotation item : existingItems) {

			// Extract base part before last underscore (“_QT…”)
			String oldLineId = item.getAdmintasklineId();
			String newLineId;

			if (oldLineId != null && oldLineId.contains("_QT")) {
				newLineId = oldLineId.substring(0, oldLineId.lastIndexOf("_")) + "_" + workOrderId;
			} else {
				newLineId = oldLineId + "_" + workOrderId; // fallback
			}

			ServiceRequestPaintQuotation2 q2 = new ServiceRequestPaintQuotation2();
			q2.setWorkOrderLineId(newLineId); // ✅ replaced with WO ID
			q2.setServiceCategory(item.getServiceCategory());
			q2.setTaskDescription(item.getTaskDescription());
			q2.setQuotationId(item.getQuotationId());
			q2.setTaskId(item.getTaskId());
			q2.setQuotedDate(item.getQuotedDate());
			q2.setMeasureNames(item.getMeasureNames());
			q2.setValue(item.getValue());
			q2.setStatus(item.getStatus());
			q2.setUpdatedBy(userInfo.userId);
			q2.setUpdatedDate(new Date());
			q2.setSpId(item.getSpId());
			q2.setWorkOrderId(workOrderId); // ✅ new WO ID
			newQuotationDetails.add(q2);
		}

		serviceRequestPaintQuotationRepository2.saveAll(newQuotationDetails);

		// Step 5: Prepare JSON response data
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("workerOrderHeader", newHeader);
		responseData.put("workerOrderDetails", newQuotationDetails);

		return new GenericResponse<>("Quotation successfully duplicated with Work Order ID: " + workOrderId, true,
				responseData);
	}

	private static class UserInfo {

		String userId;

		UserInfo(String userId) {
			this.userId = userId;
		}
	}

	private UserInfo getLoggedInUserInfo(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));
		String userId;

		if (userType == UserType.Adm) {
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getEmail(); // or any other logic you want
		} else {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}

		return new UserInfo(userId);
	}

	@Override
	public Page<ServiceRequestHeaderAllQuotation2> getHeaderWorkOrder(
	        String workOrderId, String quotationId, String fromDate, String toDate,
	        String spId, String status, Pageable pageable) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // --- MAIN QUERY ---
	    CriteriaQuery<ServiceRequestHeaderAllQuotation2> query =
	            cb.createQuery(ServiceRequestHeaderAllQuotation2.class);
	    Root<ServiceRequestHeaderAllQuotation2> root =
	            query.from(ServiceRequestHeaderAllQuotation2.class);

	    List<Predicate> predicates = new ArrayList<>();

	    if (workOrderId != null && !workOrderId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workOrderId"), workOrderId));
	    }
	    if (quotationId != null && !quotationId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("quotationId"), quotationId));
	    }

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date from = null, to = null;
	    try {
	        if (fromDate != null && !fromDate.trim().isEmpty()) from = sdf.parse(fromDate);
	        if (toDate != null && !toDate.trim().isEmpty()) to = sdf.parse(toDate);
	    } catch (ParseException e) {
	        throw new RuntimeException("Invalid date format. Expected yyyy-MM-dd");
	    }

	    if (spId != null && !spId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("spId"), spId));
	    }

	    if (from != null && to != null) {
	        Date toEnd = new Date(to.getTime() + (24 * 60 * 60 * 1000) - 1);
	        predicates.add(cb.between(root.get("quotedDate"), from, toEnd));
	    } else if (from != null) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("quotedDate"), from));
	    } else if (to != null) {
	        Date toEnd = new Date(to.getTime() + (24 * 60 * 60 * 1000) - 1);
	        predicates.add(cb.lessThanOrEqualTo(root.get("quotedDate"), toEnd));
	    }

	    if (status != null && !status.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("status"), status));
	    }

	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

	    TypedQuery<ServiceRequestHeaderAllQuotation2> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // --- COUNT QUERY ---
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<ServiceRequestHeaderAllQuotation2> countRoot = countQuery.from(ServiceRequestHeaderAllQuotation2.class);
	    List<Predicate> countPredicates = new ArrayList<>();

	    if (workOrderId != null && !workOrderId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workOrderId"), workOrderId));
	    }
	    if (quotationId != null && !quotationId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("quotationId"), quotationId));
	    }
	    if (spId != null && !spId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("spId"), spId));
	    }

	    if (from != null && to != null) {
	        Date toEnd = new Date(to.getTime() + (24 * 60 * 60 * 1000) - 1);
	        countPredicates.add(cb.between(countRoot.get("quotedDate"), from, toEnd));
	    } else if (from != null) {
	        countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("quotedDate"), from));
	    } else if (to != null) {
	        Date toEnd = new Date(to.getTime() + (24 * 60 * 60 * 1000) - 1);
	        countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("quotedDate"), toEnd));
	    }

	    if (status != null && !status.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("status"), status));
	    }

	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Override
	public List<ServiceRequestPaintQuotation2> getWorkOrderDetails(
	        String workOrderLineId,
	        String taskDescription,
	        String serviceCategory,
	        String taskId,
	        String measureNames,
	        String status,
	        String spId,
	        String quotationId,
	        String workOrderId) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<ServiceRequestPaintQuotation2> query = cb.createQuery(ServiceRequestPaintQuotation2.class);
	    Root<ServiceRequestPaintQuotation2> root = query.from(ServiceRequestPaintQuotation2.class);

	    List<Predicate> predicates = new ArrayList<>();

	    if (workOrderLineId != null && !workOrderLineId.isEmpty())
	        predicates.add(cb.equal(root.get("workOrderLineId"), workOrderLineId));

	    if (taskDescription != null && !taskDescription.isEmpty())
	        predicates.add(cb.like(root.get("taskDescription"), "%" + taskDescription + "%"));

	    if (serviceCategory != null && !serviceCategory.isEmpty())
	        predicates.add(cb.equal(root.get("serviceCategory"), serviceCategory));

	    if (taskId != null && !taskId.isEmpty())
	        predicates.add(cb.equal(root.get("taskId"), taskId));

	    if (measureNames != null && !measureNames.isEmpty())
	        predicates.add(cb.equal(root.get("measureNames"), measureNames));

	    if (status != null && !status.isEmpty())
	        predicates.add(cb.equal(root.get("status"), SPWAStatus.valueOf(status)));

	    if (spId != null && !spId.isEmpty())
	        predicates.add(cb.equal(root.get("spId"), spId));

	    if (quotationId != null && !quotationId.isEmpty())
	        predicates.add(cb.equal(root.get("quotationId"), quotationId));

	    if (workOrderId != null && !workOrderId.isEmpty())
	        predicates.add(cb.equal(root.get("workOrderId"), workOrderId));

	    // if quotationId comes from another table or relation, handle separately
	    // e.g., join with quotation entity

	    query.where(cb.and(predicates.toArray(new Predicate[0])));
	    query.orderBy(cb.desc(root.get("quotedDate")));

	    return entityManager.createQuery(query).getResultList();
	}

	@Override
	public List<ServiceRequestPaintQuotation2> updateWorkOrderQuotation(
	        String workOrderId,
	        List<ServiceRequestPaintQuotation2> dtoList,
	        RegSource regSource) {

	    UserInfo userInfo = getLoggedInUserInfo(regSource);

	    // ✅ Step 1: Fetch parent header by workOrderId
	    Optional<ServiceRequestHeaderAllQuotation2> headerOpt =
	            serviceRequestHeaderAllQuotationRepo2.findByWorkOrderId(workOrderId);

	    if (headerOpt.isEmpty()) {
	        throw new ResourceNotFoundException("Header not found for workOrderId: " + workOrderId);
	    }

	    ServiceRequestHeaderAllQuotation2 header = headerOpt.get();

	    // ✅ Step 2: Fetch child quotations linked to this workOrderId
	    List<ServiceRequestPaintQuotation2> existingQuotations =
	            serviceRequestPaintQuotationRepository2.findByWorkOrderId(workOrderId);

	    if (existingQuotations.isEmpty()) {
	        throw new ResourceNotFoundException("No quotation details found for workOrderId: " + workOrderId);
	    }

	    Map<String, ServiceRequestPaintQuotation2> existingMap = existingQuotations.stream()
	            .collect(Collectors.toMap(ServiceRequestPaintQuotation2::getWorkOrderLineId, Function.identity()));

	    List<ServiceRequestPaintQuotation2> updatedQuotations = new ArrayList<>();

	    // ✅ Step 3: Update child details based on admintasklineId
	    for (ServiceRequestPaintQuotation2 dto : dtoList) {
	        ServiceRequestPaintQuotation2 existing = existingMap.get(dto.getWorkOrderLineId());

	        if (existing != null) {
	            existing.setMeasureNames(dto.getMeasureNames());
	            existing.setValue(dto.getValue());
	            existing.setUpdatedBy(userInfo.userId);
	            existing.setUpdatedDate(new Date());

	            updatedQuotations.add(serviceRequestPaintQuotationRepository2.save(existing));
	        } else {
	            throw new ResourceNotFoundException(
	                    "Quotation line not found for admintasklineId: " + dto.getWorkOrderLineId());
	        }
	    }

	    // ✅ Step 4: Update header (audit/update tracking)
	    header.setUpdatedBy(userInfo.userId);
	    header.setUpdatedDate(new Date());
	    serviceRequestHeaderAllQuotationRepo2.save(header);

	    return updatedQuotations;
	}

	@Override
	public ResponseGetWorkOrderSRHdrAndCustomerDto getWorkOrderWithCustomerDetails(
	        String workOrderId, String quotationId, String fromQuotatedDate, String toQuotatedDate,
	        String status, String spId, String userid, String userEmail, String userMobile, Pageable pageable) {

	    ResponseGetWorkOrderSRHdrAndCustomerDto response = new ResponseGetWorkOrderSRHdrAndCustomerDto();

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<ServiceRequestHeaderAllQuotation2> cq = cb.createQuery(ServiceRequestHeaderAllQuotation2.class);
	    Root<ServiceRequestHeaderAllQuotation2> root = cq.from(ServiceRequestHeaderAllQuotation2.class);

	    List<Predicate> predicates = new ArrayList<>();

	    if (workOrderId != null && !workOrderId.isEmpty())
	        predicates.add(cb.equal(root.get("workOrderId"), workOrderId));

	    if (quotationId != null && !quotationId.isEmpty())
	        predicates.add(cb.equal(root.get("quotationId"), quotationId));

	    if (status != null && !status.isEmpty())
	        predicates.add(cb.equal(root.get("status"), status));

	    if (spId != null && !spId.isEmpty())
	        predicates.add(cb.equal(root.get("spId"), spId));

	    if (fromQuotatedDate != null && toQuotatedDate != null) {
	        try {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate fromDate = LocalDate.parse(fromQuotatedDate, formatter);
	            LocalDate toDate = LocalDate.parse(toQuotatedDate, formatter);

	            // Convert LocalDate to LocalDateTime and then to Timestamp
	            Timestamp fromTimestamp = Timestamp.valueOf(fromDate.atStartOfDay());
	            Timestamp toTimestamp = Timestamp.valueOf(toDate.atTime(23, 59, 59)); // include full day

	            predicates.add(cb.between(root.get("quotedDate"), fromTimestamp, toTimestamp));
	        } catch (DateTimeParseException e) {
	            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd");
	        }
	    }


	    cq.where(predicates.toArray(new Predicate[0]));
	    cq.orderBy(cb.desc(root.get("updatedDate")));

	    TypedQuery<ServiceRequestHeaderAllQuotation2> query = entityManager.createQuery(cq);
	    query.setFirstResult((int) pageable.getOffset());
	    query.setMaxResults(pageable.getPageSize());

	    List<ServiceRequestHeaderAllQuotation2> workOrders = query.getResultList();

	    if (workOrders.isEmpty()) {
	        response.setMessage("No Work Orders Found!");
	        response.setStatus(false);
	        return response;
	    }

	    // ✅ Collect quotationIds → requestIds → requestedBy (userIds)
	    List<String> quotationIds = workOrders.stream()
	            .map(ServiceRequestHeaderAllQuotation2::getQuotationId)
	            .filter(Objects::nonNull)
	            .toList();

	    List<ServiceRequestHeaderAllQuotation> quotations =
	            serviceRequestHeaderAllQuotationRepo.findByQuotationIdIn(quotationIds);

	    List<String> requestIds = quotations.stream()
	            .map(ServiceRequestHeaderAllQuotation::getRequestId)
	            .filter(Objects::nonNull)
	            .toList();

	    List<ServiceRequest> serviceRequests = serviceRequestRepo.findByRequestIdIn(requestIds);

	    List<String> requestedByIds = serviceRequests.stream()
	            .map(ServiceRequest::getRequestedBy)
	            .filter(Objects::nonNull)
	            .toList();

	    // ✅ Fetch customers based on filters
	    List<CustomerRegistration> customers = customerRegistrationRepo.findByUserFilters(userid, userEmail, userMobile, requestedByIds);

	    // ✅ Map Work Order Headers (no nested customer)
	    List<WorkOrderCustomerResponseDto> workOrderDtos = workOrders.stream()
	            .map(wo -> new WorkOrderCustomerResponseDto(
	                    wo.getWorkOrderId(),
	                    wo.getQuotationId(),
	                    wo.getQuotedDate() != null ? wo.getQuotedDate().toString() : null,
	                    wo.getStatus(),
	                    wo.getSpId(),
	                    wo.getUpdatedBy(),
	                    wo.getUpdatedDate() != null ? wo.getUpdatedDate().toString() : null
	            ))
	            .collect(Collectors.toList());

	    // ✅ Map customers list
	    List<CustomerBasicDto> customerDtos = customers.stream()
	            .map(c -> new CustomerBasicDto(c.getUserid(), c.getUserEmail(), c.getUserMobile()))
	            .collect(Collectors.toList());

	    // ✅ Set in response
	    response.setMessage("Data fetched successfully!");
	    response.setStatus(true);
	    response.setWorkSRHeaderQuotation(workOrderDtos);
	    response.setCustomerBasicDtos(customerDtos);
	    response.setCurrentPage(pageable.getPageNumber());
	    response.setPageSize(pageable.getPageSize());
	    response.setTotalElements(workOrderDtos.size());
	    response.setTotalPages((int) Math.ceil((double) workOrderDtos.size() / pageable.getPageSize()));

	    return response;
	}


}
