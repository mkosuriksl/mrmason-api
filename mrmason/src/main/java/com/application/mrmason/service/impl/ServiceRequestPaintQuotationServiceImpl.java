package com.application.mrmason.service.impl;

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
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
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

	@Override
	public List<ServiceRequestPaintQuotation> createServiceRequestPaintQuotationService(String requestId,
			String serviceCategory, List<ServiceRequestItem> items, RegSource regSource) {

		UserInfo userInfo = getLoggedInUserInfo(regSource);

		Map<String, Integer> taskCounters = new HashMap<>(); // counter per taskId
		List<ServiceRequestPaintQuotation> savedQuotations = new ArrayList<>();

		List<ServiceRequestHeaderAllQuotation> existingAuditOpt = serviceRequestHeaderAllQuotationRepo
				.findByRequestId(requestId);
		ServiceRequestHeaderAllQuotation audit = existingAuditOpt.isEmpty() ? new ServiceRequestHeaderAllQuotation()
				: existingAuditOpt.get(0);

		if (audit.getQuotationId() == null) {
			// When new record
			audit.setQuotationId("QT" + System.currentTimeMillis());
		}
		audit.setRequestId(requestId);
		audit.setQuotedDate(new Date());
		audit.setUpdatedBy(userInfo.userId);
		audit.setUpdatedDate(new Date());
		audit.setSpId(userInfo.userId);
		audit = serviceRequestHeaderAllQuotationRepo.save(audit);
		String quotationId = audit.getQuotationId();

		for (ServiceRequestItem item : items) {
			String taskId = item.getTaskId();
			taskCounters.putIfAbsent(taskId, 0); // initialize if not present

			for (MeasurementDTO measurement : item.getMeasurements()) {
				int currentCounter = taskCounters.compute(taskId, (k, v) -> v + 1); // increment for this taskId
				String lineId = taskId + "_" + String.format("%04d", currentCounter)+"_"+quotationId; // e.g., PLUMBING_0001

				ServiceRequestPaintQuotation sRPQ = new ServiceRequestPaintQuotation();
				sRPQ.setAdmintasklineId(lineId); // ✅ Fix: Set primary key manually
				sRPQ.setRequestId(requestId);
				sRPQ.setTaskId(taskId); // Save original taskId if needed
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
			int page, int size) {

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
		}

		response.put("items", paginatedItems);
		response.put("currentPage", page);
		response.put("pageSize", size);
		response.put("totalItems", totalItems);
		response.put("totalPages", totalPages);

		return response;
	}

}
