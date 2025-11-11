package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.MeasurementDTO;
import com.application.mrmason.dto.ServiceRequestItem;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.ServiceRequestHeaderAllQuotationRepo;
import com.application.mrmason.repository.ServiceRequestPaintQuotationRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.ServiceRequestPaintQuotationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ServiceRequestPaintQuotationServiceImpl implements ServiceRequestPaintQuotationService {

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ServiceRequestPaintQuotationRepository serviceRequestPaintQuotationRepository;

	@Autowired
	ServiceRequestHeaderAllQuotationRepo serviceRequestHeaderAllQuotationRepo;

	@Autowired
	private CustomerRegistrationRepo customerRegistrationRepo;

	@Override
	public List<ServiceRequestPaintQuotation> createServiceRequestPaintQuotationService(String requestId,
			String serviceCategory, List<ServiceRequestItem> items, RegSource regSource) {

		UserInfo userInfo = getLoggedInUserInfo(regSource);

		Map<String, Integer> taskCounters = new HashMap<>();
		List<ServiceRequestPaintQuotation> savedQuotations = new ArrayList<>();

		// ✅ Find header by both requestId and spId
		List<ServiceRequestHeaderAllQuotation> existingHeaders = serviceRequestHeaderAllQuotationRepo
				.findByRequestIdAndSpId(requestId, userInfo.userId);

		ServiceRequestHeaderAllQuotation header;

		if (existingHeaders.isEmpty()) {
			// ✅ Create new quotation header if not exists for this SP
			header = new ServiceRequestHeaderAllQuotation();
			header.setQuotationId("QT" + System.currentTimeMillis());
		} else {
			// ✅ Use existing one for same SP and request
			header = existingHeaders.get(0);
		}

		header.setRequestId(requestId);
		header.setQuotedDate(new Date());
		header.setUpdatedBy(userInfo.userId);
		header.setUpdatedDate(new Date());
		header.setStatus(SPWAStatus.NEW);
		header.setSpId(userInfo.userId);
		header = serviceRequestHeaderAllQuotationRepo.save(header);

		String quotationId = header.getQuotationId();

		// Generate line items
		for (ServiceRequestItem item : items) {
			String taskId = item.getTaskId();
			taskCounters.putIfAbsent(taskId, 0);

			for (MeasurementDTO measurement : item.getMeasurements()) {
				int currentCounter = taskCounters.compute(taskId, (k, v) -> v + 1);
				String lineId = taskId + "_" + String.format("%04d", currentCounter) + "_" + quotationId;

				ServiceRequestPaintQuotation sRPQ = new ServiceRequestPaintQuotation();
				sRPQ.setAdmintasklineId(lineId);
				sRPQ.setRequestId(requestId);
				sRPQ.setQuotationId(quotationId);
				sRPQ.setTaskId(taskId);
				sRPQ.setTaskDescription(
						item.getTaskDescription() != null ? item.getTaskDescription() : item.getTaskId());
				sRPQ.setServiceCategory(serviceCategory);
				sRPQ.setQuotedDate(new Date());
				sRPQ.setStatus(SPWAStatus.NEW);
				sRPQ.setSpId(userInfo.userId);
				sRPQ.setUpdatedBy(userInfo.userId);
				sRPQ.setUpdatedDate(new Date());
				sRPQ.setMeasureNames(measurement.getMeasureNames());
				sRPQ.setValue(measurement.getValue());
				savedQuotations.add(serviceRequestPaintQuotationRepository.save(sRPQ));
			}
		}

		return savedQuotations;
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
	public Page<ServiceRequestPaintQuotation> getServiceRequestPaintQuotationService(String admintasklineId,
			String taskDescription, String taskId, String serviceCategory, String measureNames, String status,
			String spId, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// === Main query ===
		CriteriaQuery<ServiceRequestPaintQuotation> query = cb.createQuery(ServiceRequestPaintQuotation.class);
		Root<ServiceRequestPaintQuotation> root = query.from(ServiceRequestPaintQuotation.class);
		List<Predicate> predicates = new ArrayList<>();

		if (admintasklineId != null && !admintasklineId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("admintasklineId"), admintasklineId));
		}
		if (taskDescription != null && !taskDescription.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("taskDescription"), taskDescription));
		}
		if (taskId != null && !taskId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("taskId"), taskId));
		}
		if (measureNames != null) {
			predicates.add(cb.equal(root.get("measureNames"), measureNames));
		}
		if (status != null && !status.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("status"), status));
		}
		if (spId != null && !spId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("spId"), spId));
		}
		if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("serviceCategory"), serviceCategory));
		}
		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<ServiceRequestPaintQuotation> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// === Count query ===
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ServiceRequestPaintQuotation> countRoot = countQuery.from(ServiceRequestPaintQuotation.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (admintasklineId != null && !admintasklineId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("admintasklineId"), admintasklineId));
		}
		if (taskDescription != null && !taskDescription.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("taskDescription"), taskDescription));
		}
		if (taskId != null && !taskId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("taskId"), taskId));
		}
		if (measureNames != null) {
			countPredicates.add(cb.equal(countRoot.get("measureNames"), measureNames));
		}
		if (status != null && !status.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("status"), status));
		}
		if (spId != null && !spId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("spId"), spId));
		}
		if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("serviceCategory"), serviceCategory));
		}
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Override
	public List<ServiceRequestPaintQuotation> updateServiceRequestQuotation(String taskId,
			List<ServiceRequestPaintQuotation> dtoList, RegSource regSource) {

		UserInfo userInfo = getLoggedInUserInfo(regSource);

		// Step 1: Load existing quotations by taskId
		Map<String, ServiceRequestPaintQuotation> existingMap = serviceRequestPaintQuotationRepository
				.findByTaskId(taskId).stream()
				.collect(Collectors.toMap(ServiceRequestPaintQuotation::getAdmintasklineId, Function.identity()));

		List<ServiceRequestPaintQuotation> updatedQuotations = new ArrayList<>();
		String requestId = null;
		for (ServiceRequestPaintQuotation dto : dtoList) {
			String admintasklineId = dto.getAdmintasklineId();
			String lineTaskId = admintasklineId.split("_")[0];
			if (!lineTaskId.equals(taskId)) {
				throw new IllegalArgumentException(
						"Invalid admintasklineId: " + admintasklineId + " does not match taskId: " + taskId);
			}

			ServiceRequestPaintQuotation existing = existingMap.get(admintasklineId);
			if (existing != null) {
				existing.setQuotedDate(new Date());
				existing.setMeasureNames(dto.getMeasureNames());
				existing.setValue(dto.getValue());
				existing.setUpdatedBy(userInfo.userId);
				existing.setUpdatedDate(new Date());
				requestId = existing.getRequestId(); // Save for audit header update

				updatedQuotations.add(serviceRequestPaintQuotationRepository.save(existing));
			}
		}

		// Step 2: Update or create ServiceRequestQuotation audit/header
		if (requestId != null) {
			List<ServiceRequestHeaderAllQuotation> optionalHeader = serviceRequestHeaderAllQuotationRepo
					.findByRequestIds(requestId);
			ServiceRequestHeaderAllQuotation header;

			if (!optionalHeader.isEmpty()) {
				header = optionalHeader.get(0);
			} else {
				header = new ServiceRequestHeaderAllQuotation();
				header.setRequestId(requestId);
				header.setQuotedDate(new Date());
				header.setSpId(userInfo.userId);
			}
			header.setUpdatedBy(userInfo.userId);
			header.setUpdatedDate(new Date());

			serviceRequestHeaderAllQuotationRepo.save(header);
		}

		return updatedQuotations;
	}

	@Override
	public Map<String, Object> getAllGroupedQuotations(String admintasklineId, String taskDescription,
			String serviceCategory, String taskId, String measureNames, String status, String spId, String requestId,
			String quotationId, RegSource regSource, int page, int size) throws AccessDeniedException {

		SecurityInfo securityInfo = getLoggedInCustomerAndServiceAndAdmin(regSource);

		// ALLOW only Admin or Developer, block others
		if (!securityInfo.role.equals("Adm") && !securityInfo.role.equals("Developer")
				&& !securityInfo.role.equals("EC")) {
			throw new AccessDeniedException(
					"Access denied: only Admin And Customer , Developer roles are allowed to access this resource.");
		}

		// ✅ Step 1: Fetch all records
		List<ServiceRequestPaintQuotation> allQuotations = serviceRequestPaintQuotationRepository.findAll();

		// ✅ Step 2: Apply filters safely
		List<ServiceRequestPaintQuotation> filtered = allQuotations.stream()
				.filter(q -> admintasklineId == null || q.getAdmintasklineId().equalsIgnoreCase(admintasklineId))
				.filter(q -> taskDescription == null || q.getTaskDescription().equalsIgnoreCase(taskDescription))
				.filter(q -> serviceCategory == null || q.getServiceCategory().equalsIgnoreCase(serviceCategory))
				.filter(q -> taskId == null || q.getTaskId().equalsIgnoreCase(taskId))
				.filter(q -> measureNames == null || q.getMeasureNames().equalsIgnoreCase(measureNames))
				.filter(q -> status == null || (q.getStatus() != null && q.getStatus().name().equalsIgnoreCase(status)))
				.filter(q -> requestId == null || q.getRequestId().equalsIgnoreCase(requestId))
				.filter(q -> quotationId == null || q.getQuotationId().equalsIgnoreCase(quotationId))
				.filter(q -> spId == null || q.getSpId().equalsIgnoreCase(spId)).collect(Collectors.toList());

		// ✅ Step 3: Group by taskId (derived from admintasklineId before "_")
		Map<String, List<ServiceRequestPaintQuotation>> groupedByTaskId = filtered.stream()
				.collect(Collectors.groupingBy(q -> {
					String id = q.getAdmintasklineId();
					return (id != null && id.contains("_")) ? id.split("_")[0] : id;
				}));

		// ✅ Step 4: Build structured item list
		List<Map<String, Object>> items = groupedByTaskId.entrySet().stream().map(entry -> {
			String groupedTaskId = entry.getKey();
			List<ServiceRequestPaintQuotation> group = entry.getValue();
			ServiceRequestPaintQuotation parent = group.get(0);

			Map<String, Object> item = new LinkedHashMap<>();
			item.put("taskDescription", parent.getTaskDescription());
			item.put("taskId", groupedTaskId);
			// Build measurements
			List<Map<String, Object>> measurements = group.stream().map(child -> {
				Map<String, Object> measurement = new LinkedHashMap<>();
				measurement.put("admintasklineId", child.getAdmintasklineId());
				measurement.put("measureNames", child.getMeasureNames());
				measurement.put("value", child.getValue());
				return measurement;
			}).collect(Collectors.toList());

			item.put("measurements", measurements);
			return item;
		}).collect(Collectors.toList());

		// ✅ Step 5: Handle pagination
		int totalItems = items.size();
		int totalPages = (int) Math.ceil((double) totalItems / size);
		int fromIndex = Math.min(page * size, totalItems);
		int toIndex = Math.min(fromIndex + size, totalItems);
		List<Map<String, Object>> paginatedItems = items.subList(fromIndex, toIndex);

		// ✅ Step 6: Build ordered final response
		Map<String, Object> response = new LinkedHashMap<>();

		if (!filtered.isEmpty()) {
			ServiceRequestPaintQuotation first = filtered.get(0);
			response.put("serviceCategory", first.getServiceCategory());
			response.put("requestId", first.getRequestId());
			response.put("updated_by", first.getUpdatedBy());
			response.put("updateddate", first.getUpdatedDate());
			response.put("status", first.getStatus());
			response.put("spId", first.getSpId());
			response.put("quotationId", first.getQuotationId());
		}

		response.put("items", paginatedItems);
		response.put("currentPage", page);
		response.put("pageSize", size);
		response.put("totalItems", totalItems);
		response.put("totalPages", totalPages);

		return response;
	}

	@Override
	public Page<ServiceRequestHeaderAllQuotation> getHeader(String quotationId, String requestId, String fromDate,
			String toDate, String spId, String status,RegSource regSource, Pageable pageable) throws AccessDeniedException {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		SecurityInfo securityInfo = getLoggedInCustomerAndServiceAndAdmin(regSource);

		// ALLOW only Admin or Developer, block others
		if (!securityInfo.role.equals("Adm") && !securityInfo.role.equals("Developer")
				&& !securityInfo.role.equals("EC")) {
			throw new AccessDeniedException(
					"Access denied: only Admin And Customer , Developer roles are allowed to access this resource.");
		}
		CriteriaQuery<ServiceRequestHeaderAllQuotation> query = cb.createQuery(ServiceRequestHeaderAllQuotation.class);
		Root<ServiceRequestHeaderAllQuotation> root = query.from(ServiceRequestHeaderAllQuotation.class);
		List<Predicate> predicates = new ArrayList<>();

		if (quotationId != null && !quotationId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("quotationId"), quotationId));
		}
		if (requestId != null && !requestId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("requestId"), requestId));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date from = null;
		Date to = null;
		try {
			if (fromDate != null && !fromDate.trim().isEmpty()) {
				from = sdf.parse(fromDate);
			}
			if (toDate != null && !toDate.trim().isEmpty()) {
				to = sdf.parse(toDate);
			}
		} catch (ParseException e) {
			throw new RuntimeException("Invalid date format. Expected yyyy-MM-dd");
		}
		if (spId != null && !spId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("spId"), spId));
		}
		if (from != null && to != null) {
			// Ensure inclusive range even if quotedDate has time
			Date fromStart = Date.from(from.toInstant());
			Date toEnd = new Date(to.getTime() + (24 * 60 * 60 * 1000) - 1); // add 1 day minus 1 ms
			predicates.add(cb.between(root.get("quotedDate"), fromStart, toEnd));
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
		TypedQuery<ServiceRequestHeaderAllQuotation> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ServiceRequestHeaderAllQuotation> countRoot = countQuery.from(ServiceRequestHeaderAllQuotation.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (quotationId != null && !quotationId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("quotationId"), quotationId));
		}
		if (requestId != null && !requestId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("requestId"), requestId));
		}
		if (from != null && to != null) {
			countPredicates.add(cb.between(countRoot.get("quotedDate"), from, to));
		} else if (from != null) {
			countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("quotedDate"), from));
		} else if (to != null) {
			countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("quotedDate"), to));
		}
		if (spId != null && !spId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("spId"), spId));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	private static class SecurityInfo {
		String userId;
		String role;

		SecurityInfo(String userId, String role) {
			this.userId = userId;
			this.role = role;
		}
	}

	private SecurityInfo getLoggedInCustomerAndServiceAndAdmin(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		String userId;
		String role = roleNames.get(0); // Assuming only one role

		UserType userType = UserType.valueOf(role);

		if (userType == UserType.Adm) {
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getEmail();
		} else if (userType == UserType.EC) {
			CustomerRegistration customer = customerRegistrationRepo
					.findByUserEmailAndUserTypeAndRegSources(loggedInUserEmail, userType, regSource);

			if (customer == null) {
				throw new ResourceNotFoundException("No Customer found for email: " + loggedInUserEmail + ", userType: "
						+ userType + ", regSource: " + regSource);
			}
			userId = customer.getUserid();

		} else {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}

		return new SecurityInfo(userId, role);
	}

}
