package com.application.mrmason.service.impl;

import java.io.ByteArrayOutputStream;
import java.nio.file.AccessDeniedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.HeaderQuotationStatusRequest;
import com.application.mrmason.dto.MeasurementDTO;
import com.application.mrmason.dto.ResponseGetServiceRequestHeaderQuotationDto;
import com.application.mrmason.dto.ServiceRequestItem;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation;
import com.application.mrmason.entity.ServiceRequestHeaderAllQuotationHistory;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.ServiceRequestHeaderAllQuotationHistoryRepo;
import com.application.mrmason.repository.ServiceRequestHeaderAllQuotationRepo;
import com.application.mrmason.repository.ServiceRequestPaintQuotationRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.ServiceRequestPaintQuotationService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private ServiceRequestHeaderAllQuotationHistoryRepo serviceRequestHeaderAllQuotationHistoryRepo;

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
			String toDate, String spId, String status, RegSource regSource, Pageable pageable)
			throws AccessDeniedException {
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
	public ResponseGetServiceRequestHeaderQuotationDto getHeaderWithHistory(
	        String quotationId, String requestId, String fromDate,
	        String toDate, String spId, String status,
	        RegSource regSource, Pageable pageable) throws AccessDeniedException {

	    Page<ServiceRequestHeaderAllQuotation> mainPage = getHeader(
	            quotationId, requestId, fromDate, toDate, spId, status, regSource, pageable
	    );

	    // Fetch history only if quotationId is provided
	    List<ServiceRequestHeaderAllQuotationHistory> historyResults = new ArrayList<>();
	    if (quotationId != null && !quotationId.trim().isEmpty()) {
	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<ServiceRequestHeaderAllQuotationHistory> historyQuery =
	                cb.createQuery(ServiceRequestHeaderAllQuotationHistory.class);
	        Root<ServiceRequestHeaderAllQuotationHistory> historyRoot =
	                historyQuery.from(ServiceRequestHeaderAllQuotationHistory.class);

	        historyQuery.select(historyRoot)
	                .where(cb.equal(historyRoot.get("quotationId"), quotationId));

	        historyResults = entityManager.createQuery(historyQuery).getResultList();
	    }

	    ResponseGetServiceRequestHeaderQuotationDto response = new ResponseGetServiceRequestHeaderQuotationDto();
	    response.setServiceRequestHeaderQuotation(mainPage.getContent());
	    response.setRequestHeaderAllQuotationHistories(historyResults);

	    // Pagination info for main list
	    response.setCurrentPage(mainPage.getNumber());
	    response.setPageSize(mainPage.getSize());
	    response.setTotalElements(mainPage.getTotalElements());
	    response.setTotalPages(mainPage.getTotalPages());

	    response.setStatus(true);
	    response.setMessage("Service Request Quotation header and history retrieved successfully.");

	    return response;
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

	public byte[] generateSRHPdfFromHistory(ServiceRequestHeaderAllQuotationHistory header, User customer) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			PdfWriter writer = new PdfWriter(outputStream);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);

			PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
			float fontSize = 8f; // increased slightly for readability

			// Date formatter
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			// ✅ Header Section
			document.add(new Paragraph("Service Request All Quotation Report").setFont(font).setFontSize(14).setBold()
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph("Customer: " + customer.getEmail()).setFont(font).setFontSize(fontSize));
			document.add(new Paragraph("Service Request ID: " + header.getQuotationId()).setFont(font)
					.setFontSize(fontSize));
			document.add(new Paragraph(" ")); // spacer

			// ✅ Table Section
			String[] headers = { "Quotation ID", "Request ID", "Quoted Date", "Status", "SP ID", "Updated By",
					"Updated Date" };

			Table table = new Table(headers.length).useAllAvailableWidth();

			// Table Headers
			for (String h : headers) {
				table.addHeaderCell(new Cell().add(new Paragraph(h)).setFont(font).setFontSize(fontSize).setBold()
						.setBackgroundColor(ColorConstants.LIGHT_GRAY));
			}

			// ✅ Safely format each field
			table.addCell(new Cell().add(new Paragraph(header.getQuotationId() != null ? header.getQuotationId() : "-"))
					.setFont(font).setFontSize(fontSize));

			table.addCell(new Cell().add(new Paragraph(header.getRequestId() != null ? header.getRequestId() : "-"))
					.setFont(font).setFontSize(fontSize));

			table.addCell(new Cell()
					.add(new Paragraph(header.getQuotedDate() != null ? sdf.format(header.getQuotedDate()) : "-"))
					.setFont(font).setFontSize(fontSize));

			table.addCell(new Cell().add(new Paragraph(header.getStatus() != null ? header.getStatus().name() : "-"))
					.setFont(font).setFontSize(fontSize));

			table.addCell(new Cell().add(new Paragraph(header.getSpId() != null ? header.getSpId() : "-")).setFont(font)
					.setFontSize(fontSize));

			table.addCell(new Cell().add(new Paragraph(header.getUpdatedBy() != null ? header.getUpdatedBy() : "-"))
					.setFont(font).setFontSize(fontSize));

			table.addCell(new Cell()
					.add(new Paragraph(header.getUpdatedDate() != null ? sdf.format(header.getUpdatedDate()) : "-"))
					.setFont(font).setFontSize(fontSize));

			document.add(table);

			// ✅ Footer
			document.add(new Paragraph("\nThank you,\nMr Mason Team").setFont(font).setFontSize(fontSize));

			document.close();
			return outputStream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to generate PDF", e);
		}
	}
	@Override
	public Object updateServiceRequestHeaderAllQuotation(
	        HeaderQuotationStatusRequest header,
	        RegSource regSource) {

	    // ---------------------------
	    // 1️⃣ Get Logged-In User Details
	    // ---------------------------
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

	    List<String> roleNames = loggedInRole.stream()
	            .map(GrantedAuthority::getAuthority)
	            .map(role -> role.replace("ROLE_", ""))
	            .collect(Collectors.toList());

	    UserType userType = UserType.valueOf(roleNames.get(0));
	    String userId;

	    // ---------------------------
	    // 2️⃣ Identify User ID
	    // ---------------------------
	    if (userType == UserType.EC) {
	        CustomerRegistration customer = customerRegistrationRepo
	                .findByUserEmailAndUserType(loggedInUserEmail, userType)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));

	        userId = customer.getUserid();

	    } else {
	        User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("User not found: " + loggedInUserEmail));

	        userId = user.getBodSeqNo();
	    }

	    // ---------------------------
	    // 3️⃣ Fetch Existing Quotation
	    // ---------------------------
	    ServiceRequestHeaderAllQuotation existingHeader =
	            serviceRequestHeaderAllQuotationRepo.findByQuotationId(header.getQuotationId());

	    if (existingHeader == null) {
	        throw new ResourceNotFoundException("Quotation ID not found: " + header.getQuotationId());
	    }

	    Date now = new Date();

	    // ---------------------------
	    // 4️⃣ Update Logic
	    // ---------------------------
	    if (userType == UserType.EC) {

	        // EC cannot update status or meta fields
	        log.info("Skipping status, updatedBy, updatedDate update for EC: {}", userId);

	    } else {

	        // Developer / SP / CU can update fully
	        existingHeader.setStatus(header.getStatus());
	        existingHeader.setUpdatedBy(userId);
	        existingHeader.setUpdatedDate(now);
	    }

	    // ---------------------------
	    // 5️⃣ Save Updated Main Record
	    // ---------------------------
	    ServiceRequestHeaderAllQuotation saved =
	            serviceRequestHeaderAllQuotationRepo.save(existingHeader);

	    // ---------------------------
	    // 6️⃣ Insert Into History Table
	    // ---------------------------
	    ServiceRequestHeaderAllQuotationHistory history = ServiceRequestHeaderAllQuotationHistory.builder()
	            .quotationId(saved.getQuotationId())
	            .requestId(saved.getRequestId())
	            .quotedDate(saved.getQuotedDate())
	            .status(header.getStatus())
	            .spId(saved.getSpId())
	            .updatedBy(userId)
	            .updatedDate(now)
	            .userType(userType.name())
	            .build();

	    ServiceRequestHeaderAllQuotationHistory historySaved =
	            serviceRequestHeaderAllQuotationHistoryRepo.save(history);

	    // ---------------------------
	    // 7️⃣ Prepare Email Content
	    // ---------------------------
	    String subject = "Service Request Quotation Updated Successfully";
	    String body = "Dear User,<br><br>" +
	            "The Service Request Quotation has been updated successfully.<br>" +
	            "Quotation ID: <b>" + saved.getQuotationId() + "</b><br>" +
	            "Status: <b>" + saved.getStatus() + "</b><br><br>" +
	            "Regards,<br>Mr Mason Team";

	    // ---------------------------
	    // 8️⃣ Generate PDF
	    // ---------------------------
	    User servicePerson = userDAO.findByBodSeqNos(historySaved.getSpId())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Service Person not found for ID: " + saved.getSpId()));

	    byte[] pdf = generateSRHPdfFromHistory(historySaved, servicePerson);

	    // ---------------------------
	    // 9️⃣ Collect Email Recipients
	    // ---------------------------
	    Set<String> recipients = new HashSet<>();

	    // Service Person
	    recipients.add(servicePerson.getEmail());

	    // Updated By Email
	    String updatedByEmail = null;

	    if (saved.getUpdatedBy() != null) {
	        Optional<User> updatedUserOpt = userDAO.findByBodSeqNos(saved.getUpdatedBy());

	        if (updatedUserOpt.isPresent()) {
	            updatedByEmail = updatedUserOpt.get().getEmail();
	        } else {
	            Optional<CustomerRegistration> updatedCustomerOpt =
	                    customerRegistrationRepo.findByUserids(saved.getUpdatedBy());

	            if (updatedCustomerOpt.isPresent()) {
	                updatedByEmail = updatedCustomerOpt.get().getUserEmail();
	            }
	        }
	    }

	    if (updatedByEmail != null && !updatedByEmail.isEmpty()) {
	        recipients.add(updatedByEmail);
	    }

	    // Send also to logged-in EC
	    if (userType == UserType.EC) {
	        recipients.add(loggedInUserEmail);
	    }

	    // ---------------------------
	    // 🔟 Send Emails
	    // ---------------------------
	    for (String recipient : recipients) {
	        try {
	            emailService.sendEmailWithAttachment(
	                    recipient, subject, body, pdf, "UpdatedQuotation.pdf"
	            );
	            log.info("Email sent successfully to {}", recipient);

	        } catch (Exception e) {
	            log.error("Failed to send email to {} -> {}", recipient, e.getMessage());
	        }
	    }

	    // ---------------------------
	    // 1️⃣1️⃣ Return Response Based on Role
	    // ---------------------------
	    if (userType == UserType.EC) {

	        // Return EC-only table response
	        return historySaved;
	    }

	    // Return full main table for Developer / SP / CU
	    return saved;
	}

//	public ServiceRequestHeaderAllQuotation updateServiceRequestHeaderAllQuotation(
//	        HeaderQuotationStatusRequest header, RegSource regSource) {
//
//	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//	    Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
//
//	    List<String> roleNames = loggedInRole.stream()
//	            .map(GrantedAuthority::getAuthority)
//	            .map(role -> role.replace("ROLE_", ""))
//	            .collect(Collectors.toList());
//
//	    // ✅ Identify userType (Developer also allowed now)
//	    UserType userType = UserType.valueOf(roleNames.get(0));
//	    String userId;
//
//	    // ✅ Identify the logged-in user
//	    if (userType == UserType.EC) {
//	        CustomerRegistration customer = customerRegistrationRepo
//	                .findByUserEmailAndUserType(loggedInUserEmail, userType)
//	                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));
//	        userId = customer.getUserid();
//	    } else {
//	        User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
//	                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
//	        userId = user.getBodSeqNo();
//	    }
//
//	    // ✅ Find existing quotation
//	    ServiceRequestHeaderAllQuotation existingHeader = serviceRequestHeaderAllQuotationRepo
//	            .findByQuotationId(header.getQuotationId());
//
//	    if (existingHeader == null) {
//	        throw new ResourceNotFoundException("Quotation ID not found: " + header.getQuotationId());
//	    }
//
//	    Date now = new Date();
//
//	    // ✅ Update rules by user type
//	    if (userType == UserType.EC) {
//	        // EC users are allowed, but skip restricted fields
//	        log.info("Skipping status, updatedBy, and updatedDate update for End-Customer: {}", userId);
//	    } else {
//	        // All other users (SP, CU, Developer, etc.) can update
//	        existingHeader.setStatus(header.getStatus());
//	        existingHeader.setUpdatedBy(userId);
//	        existingHeader.setUpdatedDate(now);
//	    }
//
//	    // ✅ Save main table record
//	    ServiceRequestHeaderAllQuotation saved = serviceRequestHeaderAllQuotationRepo.save(existingHeader);
//
//	    // ✅ Insert record into history table (for all user types)
//	    ServiceRequestHeaderAllQuotationHistory history = ServiceRequestHeaderAllQuotationHistory.builder()
//	            .quotationId(saved.getQuotationId())
//	            .requestId(saved.getRequestId())
//	            .quotedDate(saved.getQuotedDate())
//	            .status(userType == UserType.EC ? header.getStatus() : header.getStatus()) // EC keeps old status
//	            .spId(saved.getSpId())
//	            .updatedBy(userId)
//	            .updatedDate(now)
//	            .userType(userType.name())
//	            .build();
//
//	    ServiceRequestHeaderAllQuotationHistory historySaved=serviceRequestHeaderAllQuotationHistoryRepo.save(history);
//
//	    // ✅ Prepare email data
//	    String subject = "Service Request Quotation Updated Successfully";
//	    String body = "Dear User,<br><br>"
//	            + "The Service Request Quotation has been updated successfully.<br>"
//	            + "Quotation ID: <b>" + saved.getQuotationId() + "</b><br>"
//	            + "Status: <b>" + saved.getStatus() + "</b><br><br>"
//	            + "Regards,<br>Mr Mason Team";
//
//	    // ✅ Generate PDF once
//	    User servicePerson = userDAO.findByBodSeqNos(historySaved.getSpId())
//	            .orElseThrow(() -> new ResourceNotFoundException("Service Person not found for ID: " + saved.getSpId()));
//	    byte[] pdf = generateSRHPdfFromHistory(historySaved, servicePerson);
//
//	    // ✅ Collect all recipients (to avoid duplicate emails)
//	    Set<String> recipients = new HashSet<>();
//
//	    // 1️⃣ Service Person
//	    recipients.add(servicePerson.getEmail());
//
//	    // 2️⃣ UpdatedBy (check both tables)
//	    String updatedByEmail = null;
//	    if (saved.getUpdatedBy() != null) {
//	        Optional<User> updatedUserOpt = userDAO.findByBodSeqNos(saved.getUpdatedBy());
//	        if (updatedUserOpt.isPresent()) {
//	            updatedByEmail = updatedUserOpt.get().getEmail();
//	        } else {
//	            Optional<CustomerRegistration> updatedCustomerOpt =
//	                    customerRegistrationRepo.findByUserids(saved.getUpdatedBy());
//	            if (updatedCustomerOpt.isPresent()) {
//	                updatedByEmail = updatedCustomerOpt.get().getUserEmail();
//	            }
//	        }
//	    }
//	    if (updatedByEmail != null && !updatedByEmail.isEmpty()) {
//	        recipients.add(updatedByEmail);
//	    }
//
//	    // 3️⃣ Optionally send to logged-in EC (if EC triggered update)
//	    if (userType == UserType.EC) {
//	        recipients.add(loggedInUserEmail);
//	    }
//
//	    // ✅ Send emails
//	    for (String recipient : recipients) {
//	        try {
//	            emailService.sendEmailWithAttachment(recipient, subject, body, pdf, "UpdatedQuotation.pdf");
//	            log.info("📧 Email sent successfully to {}", recipient);
//	        } catch (Exception e) {
//	            log.error("❌ Failed to send email to {}: {}", recipient, e.getMessage());
//	        }
//	    }
//
//	    return saved;
//	}
}
