package com.application.mrmason.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.CMaterialReqHeaderDetailsDTO;
import com.application.mrmason.dto.CMaterialReqHeaderDetailsResponseDTO;
import com.application.mrmason.dto.CMaterialRequestHeaderDTO;
import com.application.mrmason.dto.CommonMaterialRequestDto;
import com.application.mrmason.dto.ResponseCMaterialReqHeaderDetailsDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CMaterialReqHeaderDetailsEntity;
import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CMaterialReqHeaderDetailsRepository;
import com.application.mrmason.repository.CMaterialRequestHeaderRepository;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.MaterialSupplierRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.CMaterialReqHeaderDetailsService;
import com.itextpdf.io.IOException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

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
public class CMaterialReqHeaderDetailsServiceImpl implements CMaterialReqHeaderDetailsService {

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	private CMaterialRequestHeaderRepository headerRepo;

	@Autowired
	private CustomerRegistrationRepo customerRegistrationRepo;

	@Autowired
	private CMaterialReqHeaderDetailsRepository detailsRepo;

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private MaterialSupplierRepository materialSupplierRepository;

//	@Override
//	public Page<CMaterialRequestHeaderDTO> getMaterialRequestsWithDetails(
//			String requestedBy,String materialRequestId, String customerEmail, String customerName, String customerMobile,
//            String deliveryLocation, LocalDate fromRequestDate, LocalDate toRequestDate,
//            LocalDate fromDeliveryDate, LocalDate toDeliveryDate,String cMatRequestIdLineid, Pageable pageable) {
//
//        Specification<CMaterialRequestHeaderEntity> spec = 
//            CMaterialRequestHeaderSpecification.filterByParams(requestedBy,
//                materialRequestId, customerEmail, customerName, customerMobile,
//                deliveryLocation, fromRequestDate, toRequestDate, fromDeliveryDate, toDeliveryDate
//            );
//
//        Page<CMaterialRequestHeaderEntity> entities = headerRepo.findAll(spec, pageable);
//        return entities.map(entity -> {
//            CMaterialRequestHeaderDTO dto = modelMapper.map(entity, CMaterialRequestHeaderDTO.class);
//
//            List<CMaterialReqHeaderDetailsEntity> details;
//
//            if (cMatRequestIdLineid != null && !cMatRequestIdLineid.isEmpty()) {
//                CMaterialReqHeaderDetailsEntity detail = detailsRepo.findById(cMatRequestIdLineid).orElse(null);
//                details = new ArrayList<>();
//                if (detail != null && detail.getCMatRequestId().equals(entity.getMaterialRequestId())) {
//                    details.add(detail);
//                }
//            } else {
//                details = detailsRepo.findByCMatRequestId(entity.getMaterialRequestId());
//            }
//
//            List<CMaterialReqHeaderDetailsDTO> detailDtos = details.stream()
//                    .map(d -> modelMapper.map(d, CMaterialReqHeaderDetailsDTO.class))
//                    .collect(Collectors.toList());
//
//            dto.setCMaterialReqHeaderDetailsEntity(detailDtos);
//            
//            List<CMaterialReqHeaderDetailsDTO> detailDto = details.stream()
//                    .map(d -> {
//                        CMaterialReqHeaderDetailsDTO dtoDetail = modelMapper.map(d, CMaterialReqHeaderDetailsDTO.class);
//
//                        // Enrich with discount from MaterialSupplier (if exists)
//                        materialSupplierRepository.findById(d.getCMatRequestIdLineid())
//                        .ifPresent(supplier -> {
//                            dtoDetail.setQuotationId(supplier.getQuotationId());
//                            dtoDetail.setCustomerOrder(supplier.getCustomerOrder());
//                            dtoDetail.setMrp(supplier.getMrp());
//                            dtoDetail.setDiscount(supplier.getDiscount());
//                            dtoDetail.setQuotedAmount(supplier.getQuotedAmount());
//                            dtoDetail.setSupplierId(supplier.getSupplierId());
//                            dtoDetail.setQuotedDate(supplier.getQuotedDate());
//                            dtoDetail.setSupplierUpdatedDate(supplier.getUpdatedDate()); 
//                            dtoDetail.setStatus(supplier.getStatus());
//                        });
//                        return dtoDetail;
//                    })
//                    .collect(Collectors.toList());
//
//                dto.setCMaterialReqHeaderDetailsEntity(detailDto);
//            return dto;
//        });
//
//    }
	
	@Override
	public Page<CMaterialRequestHeaderDTO> getMaterialRequestsWithDetails(
	        String requestedBy, String materialRequestId, String customerEmail, String customerName, String customerMobile,
	        String deliveryLocation, LocalDate fromRequestDate, LocalDate toRequestDate,
	        LocalDate fromDeliveryDate, LocalDate toDeliveryDate, String cMatRequestIdLineid,
	        String brand, String itemName, String itemSize, Pageable pageable) {

	    // If lineid is provided → resolve headerId and override materialRequestId
	    if (cMatRequestIdLineid != null && !cMatRequestIdLineid.isEmpty()) {
	        CMaterialReqHeaderDetailsEntity detail =
	                detailsRepo.findById(cMatRequestIdLineid).orElse(null);
	        if (detail != null) {
	            materialRequestId = detail.getCMatRequestId(); // restrict only to this header
	        } else {
	            return Page.empty(pageable);
	        }
	    }

	    // Pre-filter headers by brand/itemName/itemSize if needed
	    List<String> filteredHeaderIds = null;
	    if (brand != null || itemName != null || itemSize != null) {
	        filteredHeaderIds = detailsRepo.findHeaderIdsByFilters(brand, itemName, itemSize);
	        if (filteredHeaderIds == null || filteredHeaderIds.isEmpty()) {
	            return Page.empty(pageable);
	        }
	    }

	    // Build specification for header search
	    Specification<CMaterialRequestHeaderEntity> spec =
	            CMaterialRequestHeaderSpecification.filterByParams(
	                    requestedBy,
	                    materialRequestId,
	                    customerEmail,
	                    customerName,
	                    customerMobile,
	                    deliveryLocation,
	                    fromRequestDate,
	                    toRequestDate,
	                    fromDeliveryDate,
	                    toDeliveryDate
	            );

	    // Apply headerIds filter if present
	    if (filteredHeaderIds != null) {
	        List<String> finalFilteredHeaderIds = filteredHeaderIds; // effectively final for lambda
	        spec = spec.and((root, query, cb) ->
	                root.get("materialRequestId").in(finalFilteredHeaderIds));
	    }

	    Page<CMaterialRequestHeaderEntity> entities = headerRepo.findAll(spec, pageable);

	    return entities.map(entity -> {
	        CMaterialRequestHeaderDTO dto = modelMapper.map(entity, CMaterialRequestHeaderDTO.class);

	        List<CMaterialReqHeaderDetailsEntity> details;
	        if (cMatRequestIdLineid != null && !cMatRequestIdLineid.isEmpty()) {
	            details = detailsRepo.findById(cMatRequestIdLineid)
	                    .map(List::of)
	                    .orElseGet(List::of);
	        } else {
	            details = detailsRepo.findByCMatRequestId(entity.getMaterialRequestId());
	        }

	        // Apply detail filters (cleanup after fetch)
	        details = details.stream()
	                .filter(d -> brand == null || brand.equalsIgnoreCase(d.getBrand()))
	                .filter(d -> itemName == null || itemName.equalsIgnoreCase(d.getItemName()))
	                .filter(d -> itemSize == null || itemSize.equalsIgnoreCase(d.getItemSize()))
	                .collect(Collectors.toList());

	        List<CMaterialReqHeaderDetailsDTO> detailDtos = details.stream()
	                .map(d -> {
	                    CMaterialReqHeaderDetailsDTO dtoDetail =
	                            modelMapper.map(d, CMaterialReqHeaderDetailsDTO.class);

	                    materialSupplierRepository.findById(d.getCMatRequestIdLineid())
	                            .ifPresent(supplier -> {
	                                dtoDetail.setQuotationId(supplier.getQuotationId());
	                                dtoDetail.setCustomerOrder(supplier.getCustomerOrder());
	                                dtoDetail.setMrp(supplier.getMrp());
	                                dtoDetail.setDiscount(supplier.getDiscount());
	                                dtoDetail.setQuotedAmount(supplier.getQuotedAmount());
	                                dtoDetail.setSupplierId(supplier.getSupplierId());
	                                dtoDetail.setQuotedDate(supplier.getQuotedDate());
	                                dtoDetail.setSupplierUpdatedDate(supplier.getUpdatedDate());
	                                dtoDetail.setStatus(supplier.getStatus());
	                                dtoDetail.setGst(supplier.getGst());
	                            });
	                    return dtoDetail;
	                })
	                .collect(Collectors.toList());

	        dto.setCMaterialReqHeaderDetailsEntity(detailDtos);
	        return dto;
	    });
	}

	@Transactional
	@Override
	public ResponseCMaterialReqHeaderDetailsDto addMaterialRequest(CommonMaterialRequestDto request) {
	    List<CMaterialReqHeaderDetailsResponseDTO> responseList = new ArrayList<>();
	    ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();

	    try {
	        String materialCategory = request.getMaterialCategory();
	        String requestedBy = request.getRequestedBy();
	        LocalDate deliveryDate = request.getDeliveryDate();
	        String deliveryLocation = request.getDeliveryLocation();

	        String generatedRequestId = generateRequestId();
	        int totalQty = 0;

	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

	        boolean isCustomer = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_EC"));
	        boolean isServicePerson = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_Developer"));

	        if (!isCustomer && !isServicePerson) {
	            throw new AccessDeniedException("Unauthorized role");
	        }

	        // ✅ Fetch email based on token role
	        String loggedEmail = authentication.getName();
	        String recipientEmail;
	        String recipientName;
	        String recipientUserId;

	        if (isCustomer) {
	            CustomerRegistration customer = customerRegistrationRepo
	                    .findByUserEmailAndUserType(loggedEmail, UserType.EC)
	                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found for email: " + loggedEmail));
	            recipientEmail = customer.getUserEmail();
	            recipientName=customer.getUsername();
	            recipientUserId=customer.getUserid();
	        } else {
	            User developer = userDAO
	                    .findByEmailAndUserTypeAndRegSource(loggedEmail, UserType.Developer, RegSource.MRMASON)
	                    .orElseThrow(() -> new ResourceNotFoundException(
	                            "Developer not found for email: " + loggedEmail + " with RegSource: MRMASON"));
	            recipientEmail = developer.getEmail();
	            recipientName=developer.getUsername();
	            recipientUserId=developer.getBodSeqNo();

	        }

	        List<CMaterialReqHeaderDetailsDTO> requestDTOList = request.getMaterialRequests();
	        for (int i = 0; i < requestDTOList.size(); i++) {
	            CMaterialReqHeaderDetailsDTO requestDTO = requestDTOList.get(i);
	            try {
	                String lineItemId = generateLineId(generatedRequestId, i + 1);
	                log.info("Generated Line Item ID: {}", lineItemId);

	                CMaterialReqHeaderDetailsEntity entity = mapToEntity(requestDTO);

	                entity.setCMatRequestId(generatedRequestId);
	                entity.setCMatRequestIdLineid(lineItemId);
	                entity.setMaterialCategory(materialCategory);
	                entity.setRequestedBy(requestedBy);
	                entity.setOrderDate(LocalDate.now());
	                entity.setUpdatedDate(LocalDate.now());

	                CMaterialReqHeaderDetailsEntity savedEntity = detailsRepo.save(entity);
	                responseList.add(mapToResponseDTO(savedEntity));

	                totalQty += requestDTO.getQty();

	            } catch (Exception e) {
	                log.error("Error processing material request for item {}: {}", requestDTO.getItemName(), e);
	                responseList.add(buildErrorResponse(requestDTO));
	            }
	        }

	        CMaterialRequestHeaderEntity headerEntity;
	        if (isCustomer) {
	            headerEntity = createCustomerHeaderEntity(generatedRequestId, totalQty, requestedBy, deliveryDate, deliveryLocation);
	        } else {
	            headerEntity = createServicePersonHeaderEntity(generatedRequestId, totalQty, requestedBy, deliveryDate, deliveryLocation);
	        }
	        headerRepo.save(headerEntity);

	        // ✅ Prepare Response
	        response.setStatus(!responseList.isEmpty());
	        response.setMessage("Material request details processed successfully.");
	        response.setMaterialRequestDetailsList(responseList);

	        // ✅ Generate PDF
	        byte[] pdfBytes = generateMaterialRequestPdf(responseList);
	        
	        String emailBody =
	        	    "<p>Hello " + recipientName + ",</p>" +
	        	    "<p>Your material request with ID <b>" + generatedRequestId + "</b> has been created successfully.</p>" +
	        	    "<p><b>Request Details:</b></p>" +
	        	    "<ul>" +
	        	        "<li>Created By: " + recipientUserId + "</li>" +
	        	        "<li>Created Date: " + headerEntity.getCreatedDate() + "</li>" +
	        	        "<li>Total Quantity: " + headerEntity.getTotalQty() + "</li>" +
	        	    "</ul>" +
	        	    "<p>Please find the detailed request attached in PDF format.</p>" +
	        	    "<p>Thank you for using our service!</p>";

	        	emailService.sendEmailWithAttachment(
	        	    recipientEmail,
	        	    "Material Request Details - " + generatedRequestId,
	        	    emailBody,  // HTML body
	        	    pdfBytes,
	        	    "material_request_" + generatedRequestId + ".pdf"
	        	);



	    } catch (Exception e) {
	        log.error("Error processing material requests: {}", e.getMessage(), e);
	        response.setStatus(false);
	        response.setMessage("Failed to process material requests.");
	    }

	    return response;
	}

	private byte[] generateMaterialRequestPdf(List<CMaterialReqHeaderDetailsResponseDTO> requestList) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter writer = new PdfWriter(baos);
	    PdfDocument pdf = new PdfDocument(writer);
	    Document document = new Document(pdf, PageSize.A4);
	    document.setMargins(20, 20, 20, 20);

	    // Title
	    document.add(new Paragraph("Material Request Details")
	            .setBold()
	            .setFontSize(14)
	            .setTextAlignment(TextAlignment.CENTER));
	    document.add(new Paragraph("\n"));

	    // Table column widths as percentages
	    float[] columnWidths = {10, 10, 12, 10, 12, 8, 8, 10, 10, 10};
	    Table table = new Table(UnitValue.createPercentArray(columnWidths));
	    table.setWidth(UnitValue.createPercentValue(100));

	    // Table header style
	    String[] headers = {
	        "cMatRequestIdLineid", "cMatRequestId", "Material Category", "Brand",
	        "Item Name", "Item Size", "Qty", "Order Date", "Requested By", "Updated Date"
	    };

	    for (String header : headers) {
	        table.addHeaderCell(new Cell()
	                .add(new Paragraph(header))
	                .setBold()
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
	    }

	    // Table rows
	    for (CMaterialReqHeaderDetailsResponseDTO dto : requestList) {
	        table.addCell(new Cell().add(new Paragraph(dto.getCMatRequestIdLineid())).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(dto.getCMatRequestId())).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(dto.getMaterialCategory())).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(dto.getBrand())).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(dto.getItemName())).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(dto.getItemSize())).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getQty()))).setFontSize(8));
//	        table.addCell(new Cell().add(new Paragraph(dto.getOrderDate() != null ? dto.getOrderDate().toString() : "")).setFontSize(8));
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        table.addCell(new Cell().add(new Paragraph(
	            dto.getOrderDate() != null ? dto.getOrderDate().format(formatter) : ""
	        )).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(dto.getRequestedBy())).setFontSize(8));
//	        table.addCell(new Cell().add(new Paragraph(dto.getUpdatedDate() != null ? dto.getUpdatedDate().toString() : "")).setFontSize(8));
	        table.addCell(new Cell().add(new Paragraph(
	            dto.getUpdatedDate() != null ? dto.getUpdatedDate().format(formatter) : ""
	        )).setFontSize(8));
	    }

	    document.add(table);
	    document.close();

	    return baos.toByteArray();
	}

	private CMaterialRequestHeaderEntity createCustomerHeaderEntity(String requestId, int totalQty, String userid,
			LocalDate deliveryDate, String deliveryLocation) {
		CMaterialRequestHeaderEntity headerEntity = new CMaterialRequestHeaderEntity();
		headerEntity.setMaterialRequestId(requestId);
		headerEntity.setTotalQty(totalQty);
		headerEntity.setCreatedDate(LocalDate.now());
		headerEntity.setDeliveryDate(deliveryDate);
		headerEntity.setDeliveryLocation(deliveryLocation);

		CustomerRegistration customer = customerRegistrationRepo.findByUserid(userid);
		if (customer != null) {
			headerEntity.setRequestedBy(customer.getUserid());
			headerEntity.setCustomerName(customer.getCustomerName());
			headerEntity.setCustomerEmail(customer.getUserEmail());
			headerEntity.setCustomerMobile(customer.getUserMobile());
			headerEntity.setUpdatedBy(customer.getCustomerName());
		} else {
			log.warn("Customer with userId {} not found.", userid);
			headerEntity.setRequestedBy(userid);
		}

		return headerEntity;
	}

	private CMaterialRequestHeaderEntity createServicePersonHeaderEntity(String requestId, int totalQty,
			String bodSeqNo, LocalDate deliveryDate, String deliveryLocation) {
		CMaterialRequestHeaderEntity headerEntity = new CMaterialRequestHeaderEntity();
		headerEntity.setMaterialRequestId(requestId);
		headerEntity.setTotalQty(totalQty);
		headerEntity.setCreatedDate(LocalDate.now());
		headerEntity.setDeliveryDate(deliveryDate);
		headerEntity.setDeliveryLocation(deliveryLocation);

		User servicePerson = userDAO.findByBodSeqNo(bodSeqNo);
		if (servicePerson != null) {
			headerEntity.setRequestedBy(servicePerson.getBodSeqNo());
			headerEntity.setCustomerName(servicePerson.getName());
			headerEntity.setCustomerEmail(servicePerson.getEmail());
			headerEntity.setCustomerMobile(servicePerson.getMobile());
			headerEntity.setUpdatedBy(servicePerson.getName());
		} else {
			log.warn("Service person with bodSeqNo {} not found.", bodSeqNo);
			headerEntity.setRequestedBy(bodSeqNo);
		}

		return headerEntity;
	}

	private String generateRequestId() {
		LocalDateTime now = LocalDateTime.now();
		return String.format("CMM%04d%02d%02d%02d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
				now.getHour(), now.getMinute(), now.getSecond());
	}

	private String generateLineId(String requestId, int lineCounter) {
		return String.format("%s_%04d", requestId, lineCounter);
	}

	private CMaterialReqHeaderDetailsResponseDTO buildErrorResponse(CMaterialReqHeaderDetailsDTO requestDTO) {
		CMaterialReqHeaderDetailsResponseDTO errorResponse = new CMaterialReqHeaderDetailsResponseDTO();
		errorResponse.setCMatRequestId("N/A");
		errorResponse.setMaterialCategory(requestDTO.getMaterialCategory());
		errorResponse.setBrand(requestDTO.getBrand());
		errorResponse.setItemName(requestDTO.getItemName());
		errorResponse.setItemSize(requestDTO.getItemSize());
		errorResponse.setQty(requestDTO.getQty());
		errorResponse.setRequestedBy("N/A");
		errorResponse.setUpdatedDate(LocalDate.now());
		errorResponse.setCMatRequestIdLineid("N/A");
		return errorResponse;
	}

	private void sendMaterialRequestEmail(CMaterialRequestHeaderEntity entity) {
		String email = entity.getCustomerEmail();
		if (email != null && !email.isEmpty()) {
			String subject = "Material Request Created";
			String body = String.format(
					"<html><body>" + "<h3>Hello  %s,</h3>"
							+ "<p>Your material request with ID <b>%s</b> has been created successfully.</p>"
							+ "<p><b>Request Details:</b></p>" + "<ul>" + "<li>Created By: %s</li>"
							+ "<li>Created Date: %s</li>" + "<li>Total Quantity: %s</li>" + "</ul>"
							+ "<p>Thank you for using our service!</p>" + "</body></html>",
					entity.getCustomerName(), entity.getMaterialRequestId(), entity.getRequestedBy(),
					entity.getCreatedDate(), entity.getTotalQty());

			try {
				emailService.sendEmail(email, subject, body);
				log.info("Material request creation email sent to: {}", email);
			} catch (Exception e) {
				log.error("Failed to send email notification: {}", e.getMessage(), e);
			}
		} else {
			log.warn("No email address found for customer, skipping email notification.");
		}
	}

	@Transactional
	@Override
	public CMaterialReqHeaderDetailsResponseDTO updateMaterialRequestHeaderDetails(String cMatRequestIdLineid,
			CMaterialReqHeaderDetailsDTO requestDTO) {

		CMaterialReqHeaderDetailsEntity entity = detailsRepo.findById(cMatRequestIdLineid)
				.orElseThrow(() -> new RuntimeException("Material Request Header not found"));

		CMaterialRequestHeaderEntity headerEntity = headerRepo.findByMaterialRequestId(entity.getCMatRequestId());
		if (headerEntity == null) {
			throw new RuntimeException("Material Request Header not found for ID: " + entity.getCMatRequestId());
		}

		entity.setMaterialCategory(requestDTO.getMaterialCategory());
		entity.setBrand(requestDTO.getBrand());
		entity.setItemName(requestDTO.getItemName());
		entity.setItemSize(requestDTO.getItemSize());
		entity.setQty(requestDTO.getQty());
		entity.setRequestedBy(requestDTO.getRequestedBy());
		entity.setUpdatedDate(LocalDate.now());

		CMaterialReqHeaderDetailsEntity updatedEntity = detailsRepo.save(entity);
		if (updatedEntity == null || updatedEntity.getQty() != requestDTO.getQty()) {
			throw new RuntimeException("Failed to update qty in the database.");
		}

		int updatedTotalQty = detailsRepo.findByCMatRequestId(entity.getCMatRequestId()).stream()
				.mapToInt(CMaterialReqHeaderDetailsEntity::getQty).sum();

		headerEntity.setTotalQty(updatedTotalQty);
		headerRepo.save(headerEntity);

		return mapToResponseDTO(updatedEntity);
	}

//    @Override
//    public List<CMaterialReqHeaderDetailsResponseDTO> getAllMaterialRequestHeaderDetails(
//            String cMatRequestIdLineid, String cMatRequestId, String materialCategory, String brand, String itemName,
//            String itemSize, Integer qty, LocalDate orderDate, String requestedBy, LocalDate updatedDate) {
//
//        try {
//            List<CMaterialReqHeaderDetailsEntity> entities = detailsRepo.findMaterialRequestsByFilters(
//                    cMatRequestIdLineid, cMatRequestId, materialCategory, brand, itemName, itemSize, qty, orderDate,
//                    requestedBy,
//                    updatedDate);
//
//            if (entities.isEmpty()) {
//                log.warn("No material requests found for the given filters.");
//                return new ArrayList<>();
//            }
//
//            return entities.stream()
//                    .map(this::mapToResponseDTO)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            log.error("Error fetching material requests: {}", e.getMessage(), e);
//            return new ArrayList<>();
//        }
//    }

	@Override
	public Page<CMaterialReqHeaderDetailsResponseDTO> getAllMaterialRequestHeaderDetails(String cMatRequestIdLineid,
			String cMatRequestId, String materialCategory, String brand, String itemName, String itemSize, Integer qty,
			LocalDate orderDate, String requestedBy, LocalDate updatedDate, int page, int size) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CMaterialReqHeaderDetailsEntity> query = cb.createQuery(CMaterialReqHeaderDetailsEntity.class);
		Root<CMaterialReqHeaderDetailsEntity> root = query.from(CMaterialReqHeaderDetailsEntity.class);

		List<Predicate> predicates = new ArrayList<>();

		if (cMatRequestIdLineid != null)
			predicates.add(cb.equal(root.get("cMatRequestIdLineid"), cMatRequestIdLineid));
		if (cMatRequestId != null)
			predicates.add(cb.equal(root.get("cMatRequestId"), cMatRequestId));
		if (materialCategory != null)
			predicates.add(cb.equal(root.get("materialCategory"), materialCategory));
		if (brand != null)
			predicates.add(cb.equal(root.get("brand"), brand));
		if (itemName != null)
			predicates.add(cb.equal(root.get("itemName"), itemName));
		if (itemSize != null)
			predicates.add(cb.equal(root.get("itemSize"), itemSize));
		if (qty != null)
			predicates.add(cb.equal(root.get("qty"), qty));
		if (orderDate != null)
			predicates.add(cb.equal(root.get("orderDate"), orderDate));
		if (requestedBy != null)
			predicates.add(cb.equal(root.get("requestedBy"), requestedBy));
		if (updatedDate != null)
			predicates.add(cb.equal(root.get("updatedDate"), updatedDate));

		query.where(predicates.toArray(new Predicate[0]));
		query.orderBy(cb.desc(root.get("orderDate"))); // optional sort

		TypedQuery<CMaterialReqHeaderDetailsEntity> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult(page * size);
		typedQuery.setMaxResults(size);

		List<CMaterialReqHeaderDetailsEntity> results = typedQuery.getResultList();

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<CMaterialReqHeaderDetailsEntity> countRoot = countQuery.from(CMaterialReqHeaderDetailsEntity.class);
		countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
		Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

		List<CMaterialReqHeaderDetailsResponseDTO> dtoList = results.stream().map(this::mapToResponseDTO)
				.collect(Collectors.toList());

		return new PageImpl<>(dtoList, PageRequest.of(page, size), totalCount);
	}

	private CMaterialReqHeaderDetailsResponseDTO mapToResponseDTO(CMaterialReqHeaderDetailsEntity entity) {
		CMaterialReqHeaderDetailsResponseDTO responseDTO = new CMaterialReqHeaderDetailsResponseDTO();

		responseDTO.setCMatRequestId(entity.getCMatRequestId());
		responseDTO.setCMatRequestIdLineid(entity.getCMatRequestIdLineid());
		responseDTO.setMaterialCategory(entity.getMaterialCategory());
		responseDTO.setBrand(entity.getBrand());
		responseDTO.setItemName(entity.getItemName());
		responseDTO.setItemSize(entity.getItemSize());
		responseDTO.setQty(entity.getQty());
		responseDTO.setOrderDate(entity.getOrderDate());
		responseDTO.setRequestedBy(entity.getRequestedBy());
		responseDTO.setUpdatedDate(entity.getUpdatedDate());

		return responseDTO;
	}

	private CMaterialReqHeaderDetailsEntity mapToEntity(CMaterialReqHeaderDetailsDTO dto) {
		CMaterialReqHeaderDetailsEntity entity = new CMaterialReqHeaderDetailsEntity();
		entity.setMaterialCategory(dto.getMaterialCategory());
		entity.setBrand(dto.getBrand());
		entity.setItemName(dto.getItemName());
		entity.setItemSize(dto.getItemSize());
		entity.setQty(dto.getQty());
		return entity;
	}

	@Override
	public Page<CMaterialRequestHeaderEntity> getCMaterialRequestHeader(String materialRequestId, String customerEmail,
			String customerName, String customerMobile, String userId, LocalDate fromRequestDate,
			LocalDate toRequstDate,LocalDate fromDeliveryDate,LocalDate toDeliveryDate,String deliveryLocation, RegSource regSource, Pageable pageable) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInAdminSPInfo(regSource);

		// ALLOW only Admin or Developer, block others
		if (!userInfo.role.equals("Adm") && !userInfo.role.equals("Developer")) {
			throw new AccessDeniedException(
					"Access denied: only Admin or Developer roles are allowed to access this resource.");
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CMaterialRequestHeaderEntity> query = cb.createQuery(CMaterialRequestHeaderEntity.class);
		Root<CMaterialRequestHeaderEntity> root = query.from(CMaterialRequestHeaderEntity.class);
		List<Predicate> predicates = new ArrayList<>();

		if (materialRequestId != null && !materialRequestId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("materialRequestId"), materialRequestId));
		}
		if (customerEmail != null && !customerEmail.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("customerEmail"), customerEmail));
		}
		if (customerName != null && !customerName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("customerName"), customerName));
		}
		if (customerMobile != null && !customerMobile.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("customerMobile"), customerMobile));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("requestedBy"), userId));
		}
		if (deliveryLocation != null && !deliveryLocation.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("deliveryLocation"), deliveryLocation));
		}
		if (fromRequestDate != null && toRequstDate != null) {
			predicates.add(cb.between(root.get("createdDate"), fromRequestDate, toRequstDate));
		} else if (fromRequestDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), fromRequestDate));
		} else if (toRequstDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), toRequstDate));
		}
		
		if (fromDeliveryDate != null && toDeliveryDate != null) {
			predicates.add(cb.between(root.get("deliveryDate"), fromDeliveryDate, toDeliveryDate));
		} else if (fromDeliveryDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("deliveryDate"), fromDeliveryDate));
		} else if (toDeliveryDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("deliveryDate"), toDeliveryDate));
		}
		

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<CMaterialRequestHeaderEntity> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<CMaterialRequestHeaderEntity> countRoot = countQuery.from(CMaterialRequestHeaderEntity.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (materialRequestId != null && !materialRequestId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("materialRequestId"), materialRequestId));
		}
		if (customerEmail != null && !customerEmail.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("customerEmail"), customerEmail));
		}
		if (customerName != null && !customerName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("customerName"), customerName));
		}
		if (customerMobile != null && !customerMobile.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("customerMobile"), customerMobile));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("requestedBy"), userId));
		}
		if (deliveryLocation != null && !deliveryLocation.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("deliveryLocation"), deliveryLocation));
		}
		if (fromRequestDate != null && toRequstDate != null) {
			countPredicates.add(cb.between(countRoot.get("createdDate"), fromRequestDate, toRequstDate));
		} else if (fromRequestDate != null) {
			countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("createdDate"), fromRequestDate));
		} else if (toRequstDate != null) {
			countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("createdDate"), toRequstDate));
		}
		if (fromDeliveryDate != null && toDeliveryDate != null) {
			countPredicates.add(cb.between(countRoot.get("deliveryDate"), fromDeliveryDate, toDeliveryDate));
		} else if (fromDeliveryDate != null) {
			countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("deliveryDate"), fromDeliveryDate));
		} else if (toDeliveryDate != null) {
			countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("deliveryDate"), toDeliveryDate));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	private static class UserInfo {
		String userId;
		String role;

		UserInfo(String userId, String role) {
			this.userId = userId;
			this.role = role;
		}
	}

	private UserInfo getLoggedInAdminSPInfo(RegSource regSource) {
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
		} else {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}

		return new UserInfo(userId, role);
	}
	
	
}
