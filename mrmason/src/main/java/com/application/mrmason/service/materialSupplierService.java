package com.application.mrmason.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.QuotationStatusUpdateRequest;
import com.application.mrmason.dto.ResponseInvoiceAndDetailsDto;
import com.application.mrmason.entity.CMaterialReqHeaderDetailsEntity;
import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.Invoice;
import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.entity.MaterialSupplierQuotationHeader;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.enums.Status;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CMaterialReqHeaderDetailsRepository;
import com.application.mrmason.repository.InvoiceRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationHeaderRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.MaterialSupplierRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class materialSupplierService {

	@Autowired
	public AdminDetailsRepo adminRepo;
	@Autowired
	private MaterialSupplierQuotationUserDAO userDAO;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private MaterialSupplierRepository materialSupplierRepository;
	@Autowired
	private CMaterialReqHeaderDetailsRepository cMaterialReqHeaderDetailsRepository;
	@Autowired
	private MaterialSupplierQuotationHeaderRepository materialSupplierQuotationHeaderRepository;
	@Autowired
	private InvoiceRepository invoiceRepository;
	@Autowired
	private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;
	@Autowired
	private JavaMailSender mailsender;

//	@Transactional
//	public GenericResponse<List<MaterialSupplier>> saveItems(
//	        List<MaterialSupplier> materialQuotation,
//	        String cMatRequestId,String invoiceNumber,Status invoiceStatus,Status quotationStatus,LocalDate invoiceDate,
//	        RegSource regSource) {
//
//	    UserInfo userInfo = getLoggedInUserInfo(regSource);
//
//	    // ✅ Validate by Optional
//	    Optional<CMaterialReqHeaderDetailsEntity> headerOpt =
//	            cMaterialReqHeaderDetailsRepository.findFirstByCMatRequestId(cMatRequestId);
//
//	    if (headerOpt.isEmpty()) {
//	        throw new ResourceNotFoundException(
//	                "cMatRequestId not found in CMaterialReqHeaderDetailsEntity: " + cMatRequestId);
//	    }
//
//	    List<MaterialSupplier> validatedItems = new ArrayList<>();
//	    Set<String> seenLineItems = new HashSet<>();
//
//	    for (MaterialSupplier item : materialQuotation) {
//
//	        // Validate that materialLineItem starts with cMatRequestId + "_"
//	        if (!item.getMaterialLineItem().startsWith(cMatRequestId + "_")) {
//	            throw new IllegalArgumentException(
//	                "MaterialLineItem '" + item.getMaterialLineItem() +
//	                "' does not belong to cMatRequestId '" + cMatRequestId + "'");
//	        }
//
//	        item.setQuotationId(null);
//	        item.setCmatRequestId(cMatRequestId);
//
//	        if (!seenLineItems.add(item.getMaterialLineItem())) {
//	            throw new IllegalArgumentException(
//	                "Duplicate materialLineItem found in request: " + item.getMaterialLineItem());
//	        }
//
//	        boolean exists = materialSupplierRepository
//	                .existsBySupplierIdAndMaterialLineItem(userInfo.userId, item.getMaterialLineItem());
//	        if (exists) {
//	            throw new IllegalArgumentException(
//	                "MaterialLineItem already exists for this supplier: " + item.getMaterialLineItem());
//	        }
//
//	        Optional<CMaterialReqHeaderDetailsEntity> materialReqOpt =
//	                cMaterialReqHeaderDetailsRepository.findById(item.getMaterialLineItem());
//	        if (materialReqOpt.isEmpty()) {
//	            throw new ResourceNotFoundException(
//	                "MaterialLineItem not found in CMaterialReqHeaderDetailsEntity: " + item.getMaterialLineItem());
//	        }
//	        item.setInvoiceNumber(invoiceNumber);
//	        item.setInvoiceStatus(invoiceStatus);
//	        item.setQuotationStatus(quotationStatus);
//	        item.setInvoiceDate(LocalDate.now());
//	        item.setUpdatedDate(LocalDate.now());
//	        item.setSupplierId(userInfo.userId);
//	        item.setStatus(Status.QUOTED);
//	        item.setQuotedDate(LocalDate.now());
//
//	        validatedItems.add(item);
//	    }
//
//	    // ✅ Save detail quotations
//	    List<MaterialSupplier> saved = materialSupplierRepository.saveAll(validatedItems);
//
//	    // ✅ Calculate total quoted amount (sum of quotedAmount)
//	    BigDecimal totalQuotedAmount = saved.stream()
//	            .map(MaterialSupplier::getQuotedAmount)
//	            .filter(Objects::nonNull)
//	            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//	    // ✅ Save header record
//	    MaterialSupplierQuotationHeader quotationHeader = new MaterialSupplierQuotationHeader();
//	    quotationHeader.setCmatRequestId(cMatRequestId);
//	    quotationHeader.setInvoiceNumber(invoiceNumber);
//	    quotationHeader.setInvoiceStatus(invoiceStatus);
//	    quotationHeader.setInvoiceDate(LocalDate.now());
//	    quotationHeader.setQuotationStatus(quotationStatus);
//	    
//	    quotationHeader.setQuotationId(saved.get(0).getQuotationId()); // pick any from saved, or generate
//	    quotationHeader.setQuotedAmount(totalQuotedAmount);
//	    quotationHeader.setSupplierId(userInfo.userId);
//	    quotationHeader.setQuotedDate(LocalDate.now());
//	    quotationHeader.setUpdatedDate(LocalDate.now());
//	    
//	    
//	    materialSupplierQuotationHeaderRepository.save(quotationHeader);
//
//	    return new GenericResponse<>(
//	            "Material Quotations saved successfully by user: " + userInfo.userId,
//	            true,
//	            saved
//	    );
//	}

	@Transactional
	public GenericResponse<List<MaterialSupplier>> saveItems(List<MaterialSupplier> materialQuotation,
			String cMatRequestId, String invoiceNumber, Status invoiceStatus, Status quotationStatus,
			LocalDate invoiceDate, RegSource regSource) {

		UserInfo userInfo = getLoggedInUserInfo(regSource);

		// ✅ Validate header
		Optional<CMaterialReqHeaderDetailsEntity> headerOpt = cMaterialReqHeaderDetailsRepository
				.findFirstByCMatRequestId(cMatRequestId);
		if (headerOpt.isEmpty()) {
			throw new ResourceNotFoundException(
					"cMatRequestId not found in CMaterialReqHeaderDetailsEntity: " + cMatRequestId);
		}
	    String quotationId = userInfo.userId + "_" + ((int)(Math.random() * 900000) + 100000);

		List<MaterialSupplier> validatedItems = new ArrayList<>();
		Set<String> seenLineItems = new HashSet<>();

		for (MaterialSupplier item : materialQuotation) {
			if (!item.getMaterialLineItem().startsWith(cMatRequestId + "_")) {
				throw new IllegalArgumentException("MaterialLineItem '" + item.getMaterialLineItem()
						+ "' does not belong to cMatRequestId '" + cMatRequestId + "'");
			}

			item.setQuotationId(quotationId);
			item.setCmatRequestId(cMatRequestId);

			if (!seenLineItems.add(item.getMaterialLineItem())) {
				throw new IllegalArgumentException(
						"Duplicate materialLineItem found in request: " + item.getMaterialLineItem());
			}

			boolean exists = materialSupplierRepository.existsBySupplierIdAndMaterialLineItem(userInfo.userId,
					item.getMaterialLineItem());
			if (exists) {
				throw new IllegalArgumentException(
						"MaterialLineItem already exists for this supplier: " + item.getMaterialLineItem());
			}

			Optional<CMaterialReqHeaderDetailsEntity> materialReqOpt = cMaterialReqHeaderDetailsRepository
					.findById(item.getMaterialLineItem());
			if (materialReqOpt.isEmpty()) {
				throw new ResourceNotFoundException(
						"MaterialLineItem not found in CMaterialReqHeaderDetailsEntity: " + item.getMaterialLineItem());
			}

			item.setInvoiceNumber(invoiceNumber);
			item.setInvoiceStatus(invoiceStatus);
			item.setQuotationStatus(quotationStatus);
			item.setInvoiceDate(LocalDate.now());
			item.setUpdatedDate(LocalDate.now());
			item.setSupplierId(userInfo.userId);
			item.setStatus(Status.QUOTED);
			item.setQuotedDate(LocalDate.now());

			validatedItems.add(item);
		}

		// ✅ Save detail quotations
		List<MaterialSupplier> saved = materialSupplierRepository.saveAll(validatedItems);

		// ✅ Save header
		BigDecimal totalQuotedAmount = saved.stream().map(MaterialSupplier::getQuotedAmount).filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		MaterialSupplierQuotationHeader quotationHeader = new MaterialSupplierQuotationHeader();
		quotationHeader.setCmatRequestId(cMatRequestId);
		quotationHeader.setInvoiceNumber(invoiceNumber);
		quotationHeader.setInvoiceStatus(invoiceStatus);
		quotationHeader.setInvoiceDate(LocalDate.now());
		quotationHeader.setQuotationStatus(quotationStatus);
		quotationHeader.setQuotationId(quotationId);
		quotationHeader.setQuotedAmount(totalQuotedAmount);
		quotationHeader.setSupplierId(userInfo.userId);
		quotationHeader.setQuotedDate(LocalDate.now());
		quotationHeader.setUpdatedDate(LocalDate.now());

		materialSupplierQuotationHeaderRepository.save(quotationHeader);

		MaterialSupplierQuotationUser supplier = materialSupplierQuotationUserDAO.findByBodSeqNo(userInfo.userId);
		if (supplier == null) {
		    throw new ResourceNotFoundException("Supplier not found for id: " + userInfo.userId);
		}
		// ✅ Send PDF email
		sendQuotationEmail(supplier.getEmail(), saved);
		return new GenericResponse<>("Material Quotations saved successfully by user: " + userInfo.userId, true, saved);
	}

	private void sendQuotationEmail(String toMail, List<MaterialSupplier> quotations) {
		try {
			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("no_reply@kosuriers.com");
			helper.setTo(toMail);
			helper.setSubject("Your Material Supplier Quotation");

			String body = "Dear Supplier,<br><br>Please find attached your quotation details.<br><br>Regards,<br>Team";
			helper.setText(body, true);

			// ✅ attach PDF
			byte[] pdfBytes = generateQuotationPdf(quotations);
			helper.addAttachment("quotation.pdf", new ByteArrayResource(pdfBytes));

			mailsender.send(message);
		} catch (Exception e) {
		}
	}

	private byte[] generateQuotationPdf(List<MaterialSupplier> suppliers) throws Exception {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    PdfWriter writer = new PdfWriter(out);
	    PdfDocument pdf = new PdfDocument(writer);

	    // ✅ Use A4 Landscape for more width
	    Document document = new Document(pdf, PageSize.A4.rotate());
	    document.setMargins(20, 20, 20, 20);

	    PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
	    PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

	    // Title
	    Paragraph title = new Paragraph("Material Supplier Quotation")
	            .setFont(bold)
	            .setFontSize(15)
	            .setTextAlignment(TextAlignment.CENTER);
	    document.add(title);
	    document.add(new Paragraph("\n"));

	    // ✅ Flexible column widths, auto-fit page
	    float[] columnWidths = {3, 3, 3, 2, 2, 3, 3, 3, 3};
	    Table table = new Table(UnitValue.createPercentArray(columnWidths))
	            .useAllAvailableWidth();

	    // Headers
	    String[] headers = {"Material Line Item", "Quotation Id", "CmatRequest Id", "MRP",
	                        "Discount", "Quoted Amount", "Supplier Id", "Quoted Date", "gst"};

	    for (String header : headers) {
	        table.addHeaderCell(new Cell()
	                .add(new Paragraph(header).setFont(bold).setFontSize(10))
	                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
	                .setTextAlignment(TextAlignment.CENTER));
	    }

	    // Data rows
	    for (MaterialSupplier s : suppliers) {
	    	String itemName = "";
	        Optional<CMaterialReqHeaderDetailsEntity> headerOpt =
	                cMaterialReqHeaderDetailsRepository.findById(s.getMaterialLineItem());
	        if (headerOpt.isPresent()) {
	            itemName = headerOpt.get().getItemName();
	        }
	        table.addCell(new Cell().add(new Paragraph(itemName)
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(s.getQuotationId() != null ? s.getQuotationId() : "")
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(s.getCmatRequestId() != null ? s.getCmatRequestId() : "")
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(s.getMrp() != null ? s.getMrp().toString() : "")
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(s.getDiscount() != null ? s.getDiscount().toString() : "")
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(s.getQuotedAmount() != null ? s.getQuotedAmount().toString() : "")
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(s.getSupplierId() != null ? s.getSupplierId() : "")
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(s.getQuotedDate() != null ? s.getQuotedDate().toString() : "")
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	        table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getGst()))
	                .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER)));
	    }

	    document.add(table);
	    document.close();

	    return out.toByteArray();
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
		if (!roleNames.contains("MS")) {
			throw new ResourceNotFoundException(
					"Only MaterialSupplierQuotation(MS) role is allowed. Found roles: " + roleNames);
		}
		UserType userType = UserType.MS;
		MaterialSupplierQuotationUser ms = userDAO
				.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
				.orElseThrow(() -> new ResourceNotFoundException("MS not found: " + loggedInUserEmail));
		String userId = ms.getBodSeqNo();
		return new UserInfo(userId);
	}

	public List<MaterialSupplier> updateMaterial(RegSource regSource, List<MaterialSupplier> taskList)
			throws AccessDeniedException {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		List<MaterialSupplier> updatedTasks = new ArrayList<>();

		for (MaterialSupplier task : taskList) {
			String taskIdKey = task.getMaterialLineItem();

			if (taskIdKey == null || taskIdKey.isEmpty()) {
				throw new ResourceNotFoundException("material line item is required for update.");
			}

			// Fetch the existing entity
			MaterialSupplier existingTask = materialSupplierRepository.findById(taskIdKey)
					.orElseThrow(() -> new ResourceNotFoundException(
							"Material Line Item not found for MaterialSupplier: " + taskIdKey));

			// Only update allowed fields
			existingTask.setDiscount(task.getDiscount());
			existingTask.setSupplierId(userInfo.userId);
			existingTask.setUpdatedDate(LocalDate.now());
//	            existingTask.setStatus(task.getStatus());
			existingTask.setGst(task.getGst());
			existingTask.setMrp(task.getMrp());
			updatedTasks.add(existingTask);
		}

		return materialSupplierRepository.saveAll(updatedTasks);
	}

//	@Transactional
//	public void updateQuotation(RegSource regSource, QuotationUpdateRequest request) {
//
//		UserInfo userInfo = getLoggedInUserInfo(regSource);
//		MaterialSupplierQuotationHeader header = materialSupplierQuotationHeaderRepository
//				.findById(request.getCmatRequestId()).orElseThrow(() -> new RuntimeException("Header not found"));
//		header.setQuotedAmount(request.getQuotedAmount());
//		header.setInvoiceStatus(Status.valueOf(request.getStatus()));
//		header.setInvoiceNumber("INV-" + System.currentTimeMillis()); // backend generate invoice number
//		header.setInvoiceDate(LocalDate.now());
//		materialSupplierQuotationHeaderRepository.save(header);
//
//		// 2️⃣ Update details
//		for (QuotationUpdateRequest.QuotationDetail detail : request.getQuotations()) {
//			MaterialSupplier supplierDetail = materialSupplierRepository.findById(detail.getMaterialLineItem())
//					.orElseThrow(() -> new RuntimeException("Detail not found: " + detail.getMaterialLineItem()));
//			supplierDetail.setDiscount(detail.getDiscount());
//			supplierDetail.setGst(detail.getGst());
//			supplierDetail.setMrp(detail.getMrp());
//			supplierDetail.setInvoiceNumber(header.getInvoiceNumber());
//			supplierDetail.setInvoiceStatus(header.getInvoiceStatus());
//			supplierDetail.setQuotationStatus(header.getQuotationStatus());
//			supplierDetail.setUpdatedDate(LocalDate.now());
//			materialSupplierRepository.save(supplierDetail);
//		}
//
//		// 3️⃣ Save to invoice table
//		Invoice invoice = new Invoice();
//		invoice.setCmatRequestId(request.getCmatRequestId());
//		invoice.setInvoiceNumber(header.getInvoiceNumber());
//		invoice.setQuotedAmount(request.getQuotedAmount());
//		invoice.setUpdatedBy(userInfo.userId);
//		invoice.setInvoiceDate(LocalDate.now());
//		invoiceRepository.save(invoice);
//	}

	@Transactional
	public void updateQuotationStatuses(RegSource regSource, List<QuotationStatusUpdateRequest> updates) {

        for (QuotationStatusUpdateRequest req : updates) {
            
            // 1️⃣ Generate invoice number
            String invoiceNumber = "INV-" + System.currentTimeMillis();
            int updatedDetails = materialSupplierRepository.updateInvoiceStatusByQuotationId(
                    req.getQuotationId(),
                    req.getStatus(),   // This sets invoiceStatus
                    invoiceNumber);


            // 3️⃣ Update header table (invoice status + invoice number)
            int updatedHeader = materialSupplierQuotationHeaderRepository.updateInvoiceStatusByQuotationId(
                    req.getQuotationId(),
                    req.getStatus(),
                    invoiceNumber);

            // 4️⃣ (Optional) Add debug logs or validation
            if (updatedDetails == 0) {
                System.out.println("No details found for quotation: " + req.getQuotationId());
            }
            if (updatedHeader == 0) {
                System.out.println("No header found for quotation: " + req.getQuotationId());
            }
        }
	}
	public Page<MaterialSupplier> getMaterialSupplierDetails(String quotationId, String cmatRequestId,
			String materialLineItem, String supplierId, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// === Main query ===
		CriteriaQuery<MaterialSupplier> query = cb.createQuery(MaterialSupplier.class);
		Root<MaterialSupplier> root = query.from(MaterialSupplier.class);
		List<Predicate> predicates = new ArrayList<>();

		if (quotationId != null && !quotationId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("quotationId"), quotationId));
		}
		if (cmatRequestId != null && !cmatRequestId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("cmatRequestId"), cmatRequestId));
		}
		if (materialLineItem != null && !materialLineItem.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("materialLineItem"), materialLineItem));
		}
		if (supplierId != null && !supplierId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("supplierId"), supplierId));
		}
		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<MaterialSupplier> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// === Count query ===
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialSupplier> countRoot = countQuery.from(MaterialSupplier.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (quotationId != null && !quotationId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("quotationId"), quotationId));
		}
		if (cmatRequestId != null && !cmatRequestId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("cmatRequestId"), cmatRequestId));
		}
		if (materialLineItem != null && !materialLineItem.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("materialLineItem"), materialLineItem));
		}
		if (supplierId != null && !supplierId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("supplierId"), supplierId));
		}
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

//	    public Page<MaterialSupplierQuotationHeader> getQuotationsByUserMobile(
//	    		String cmatRequestId,
//	            String userMobile,
//	            String supplierId,
//	            LocalDate fromQuotedDate,
//	            LocalDate toQuotedDate,
//	            Pageable pageable) {
//
//	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//
//	        // --- Main query ---
//	        CriteriaQuery<MaterialSupplierQuotationHeader> cq = cb.createQuery(MaterialSupplierQuotationHeader.class);
//	        Root<MaterialSupplierQuotationHeader> msqhRoot = cq.from(MaterialSupplierQuotationHeader.class);
//
//	        List<Predicate> predicates = new ArrayList<>();
//
//	        // If userMobile is provided → join via subqueries (both tables)
//	        if (userMobile != null && !userMobile.trim().isEmpty()) {
//
//	            // Subquery from CustomerRegistration
//	            Subquery<String> subqueryCustomer = cq.subquery(String.class);
//	            Root<CMaterialRequestHeaderEntity> cmrRootC = subqueryCustomer.from(CMaterialRequestHeaderEntity.class);
//	            Root<CustomerRegistration> crRoot = subqueryCustomer.from(CustomerRegistration.class);
//
//	            subqueryCustomer.select(cmrRootC.get("materialRequestId"))
//	                    .where(
//	                            cb.and(
//	                                    cb.equal(cmrRootC.get("requestedBy"), crRoot.get("userid")),
//	                                    cb.equal(crRoot.get("userMobile"), userMobile)
//	                            )
//	                    );
//
//	            // Subquery from User table
//	            Subquery<String> subqueryUser = cq.subquery(String.class);
//	            Root<CMaterialRequestHeaderEntity> cmrRootU = subqueryUser.from(CMaterialRequestHeaderEntity.class);
//	            Root<User> userRoot = subqueryUser.from(User.class);
//
//	            subqueryUser.select(cmrRootU.get("materialRequestId"))
//	                    .where(
//	                            cb.and(
//	                                    cb.equal(cmrRootU.get("requestedBy"), userRoot.get("bodSeqNo")),
//	                                    cb.equal(userRoot.get("mobile"), userMobile)
//	                            )
//	                    );
//	            
//	            // Subquery from Material table
//	            Subquery<String> subqueryMaterial= cq.subquery(String.class);
//	            Root<CMaterialRequestHeaderEntity> cmrRootU1 = subqueryUser.from(CMaterialRequestHeaderEntity.class);
//	            Root<MaterialSupplierQuotationUser> userRoot1 = subqueryUser.from(MaterialSupplierQuotationUser.class);
//
//	            subqueryUser.select(cmrRootU1.get("materialRequestId"))
//	                    .where(
//	                            cb.and(
//	                                    cb.equal(cmrRootU1.get("requestedBy"), userRoot1.get("bodSeqNo")),
//	                                    cb.equal(userRoot1.get("mobile"), userMobile)
//	                            )
//	                    );
//
//	            // Combine with OR
//	            Predicate userMobilePredicate = cb.or(
//	                    msqhRoot.get("cmatRequestId").in(subqueryCustomer),
//	                    msqhRoot.get("cmatRequestId").in(subqueryUser),
//	                    msqhRoot.get("cmatRequestId").in(subqueryMaterial)
//	            );
//
//	            predicates.add(userMobilePredicate);
//	        }
//
//	        // Supplier filter
//	        if (supplierId != null && !supplierId.trim().isEmpty()) {
//	            predicates.add(cb.equal(msqhRoot.get("supplierId"), supplierId));
//	        }
//	        if (cmatRequestId != null && !cmatRequestId.trim().isEmpty()) {
//	            predicates.add(cb.equal(msqhRoot.get("cmatRequestId"), cmatRequestId));
//	        }
//
//	        // Date range filters
//	        if (fromQuotedDate != null && toQuotedDate != null) {
//	            predicates.add(cb.between(msqhRoot.get("quotedDate"), fromQuotedDate, toQuotedDate));
//	        } else if (fromQuotedDate != null) {
//	            predicates.add(cb.greaterThanOrEqualTo(msqhRoot.get("quotedDate"), fromQuotedDate));
//	        } else if (toQuotedDate != null) {
//	            predicates.add(cb.lessThanOrEqualTo(msqhRoot.get("quotedDate"), toQuotedDate));
//	        }
//
//	        cq.select(msqhRoot);
//	        if (!predicates.isEmpty()) {
//	            cq.where(cb.and(predicates.toArray(new Predicate[0])));
//	        }
//
//	        TypedQuery<MaterialSupplierQuotationHeader> query = entityManager.createQuery(cq);
//	        query.setFirstResult((int) pageable.getOffset());
//	        query.setMaxResults(pageable.getPageSize());
//	        List<MaterialSupplierQuotationHeader> results = query.getResultList();
//
//	        // --- Count query ---
//	        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//	        Root<MaterialSupplierQuotationHeader> countRoot = countQuery.from(MaterialSupplierQuotationHeader.class);
//
//	        List<Predicate> countPredicates = new ArrayList<>();
//
//	        if (userMobile != null && !userMobile.trim().isEmpty()) {
//
//	            // Count subquery (Customer)
//	            Subquery<String> countSubqueryCustomer = countQuery.subquery(String.class);
//	            Root<CMaterialRequestHeaderEntity> cmrRootCountC = countSubqueryCustomer.from(CMaterialRequestHeaderEntity.class);
//	            Root<CustomerRegistration> crRootCount = countSubqueryCustomer.from(CustomerRegistration.class);
//
//	            countSubqueryCustomer.select(cmrRootCountC.get("materialRequestId"))
//	                    .where(
//	                            cb.and(
//	                                    cb.equal(cmrRootCountC.get("requestedBy"), crRootCount.get("userid")),
//	                                    cb.equal(crRootCount.get("userMobile"), userMobile)
//	                            )
//	                    );
//
//	            // Count subquery (User)
//	            Subquery<String> countSubqueryUser = countQuery.subquery(String.class);
//	            Root<CMaterialRequestHeaderEntity> cmrRootCountU = countSubqueryUser.from(CMaterialRequestHeaderEntity.class);
//	            Root<User> userRootCount = countSubqueryUser.from(User.class);
//
//	            countSubqueryUser.select(cmrRootCountU.get("materialRequestId"))
//	                    .where(
//	                            cb.and(
//	                                    cb.equal(cmrRootCountU.get("requestedBy"), userRootCount.get("bodSeqNo")),
//	                                    cb.equal(userRootCount.get("mobile"), userMobile)
//	                            )
//	                    );
//	            // Count subquery (Material)
//
//	            Subquery<String> countSubqueryMaterial= countQuery.subquery(String.class);
//	            Root<CMaterialRequestHeaderEntity> cmrRootCountU1 = countSubqueryMaterial.from(CMaterialRequestHeaderEntity.class);
//	            Root<MaterialSupplierQuotationUser> userRootCount1 = countSubqueryMaterial.from(MaterialSupplierQuotationUser.class);
//
//	            countSubqueryMaterial.select(cmrRootCountU1.get("materialRequestId"))
//	                    .where(
//	                            cb.and(
//	                                    cb.equal(cmrRootCountU1.get("requestedBy"), userRootCount1.get("bodSeqNo")),
//	                                    cb.equal(cmrRootCountU1.get("mobile"), userMobile)
//	                            )
//	                    );
//
//	            countPredicates.add(
//	                    cb.or(
//	                            countRoot.get("cmatRequestId").in(countSubqueryCustomer),
//	                            countRoot.get("cmatRequestId").in(countSubqueryUser),
//	                            countRoot.get("cmatRequestId").in(countSubqueryMaterial)
//
//	                    )
//	            );
//	        }
//
//	        // Supplier filter
//	        
//	        if (supplierId != null && !supplierId.trim().isEmpty()) {
//	            countPredicates.add(cb.equal(countRoot.get("supplierId"), supplierId));
//	        }
//	        if (cmatRequestId != null && !cmatRequestId.trim().isEmpty()) {
//	            countPredicates.add(cb.equal(countRoot.get("cmatRequestId"), cmatRequestId));
//	        }
//
//	        // Date range filters for count query
//	        if (fromQuotedDate != null && toQuotedDate != null) {
//	            countPredicates.add(cb.between(countRoot.get("quotedDate"), fromQuotedDate, toQuotedDate));
//	        } else if (fromQuotedDate != null) {
//	            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("quotedDate"), fromQuotedDate));
//	        } else if (toQuotedDate != null) {
//	            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("quotedDate"), toQuotedDate));
//	        }
//
//	        countQuery.select(cb.count(countRoot));
//	        if (!countPredicates.isEmpty()) {
//	            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
//	        }
//
//	        Long total = entityManager.createQuery(countQuery).getSingleResult();
//
//	        return new PageImpl<>(results, pageable, total);
//	    }
	public Page<MaterialSupplierQuotationHeader> getQuotationsByUserMobile(String cmatRequestId, String userMobile,
			String supplierId, LocalDate fromQuotedDate, LocalDate toQuotedDate, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// --------------------------
		// Main query
		// --------------------------
		CriteriaQuery<MaterialSupplierQuotationHeader> cq = cb.createQuery(MaterialSupplierQuotationHeader.class);
		Root<MaterialSupplierQuotationHeader> msqhRoot = cq.from(MaterialSupplierQuotationHeader.class);

		List<Predicate> predicates = new ArrayList<>();

		// ---- User Mobile Subqueries ----
		if (userMobile != null && !userMobile.trim().isEmpty()) {

			// Subquery 1: CustomerRegistration
			Subquery<String> subqueryCustomer = cq.subquery(String.class);
			Root<CMaterialRequestHeaderEntity> cmrRootC = subqueryCustomer.from(CMaterialRequestHeaderEntity.class);
			Root<CustomerRegistration> crRoot = subqueryCustomer.from(CustomerRegistration.class);
			subqueryCustomer.select(cmrRootC.get("materialRequestId"))
					.where(cb.and(cb.equal(cmrRootC.get("requestedBy"), crRoot.get("userid")),
							cb.equal(crRoot.get("userMobile"), userMobile)));

			// Subquery 2: User
			Subquery<String> subqueryUser = cq.subquery(String.class);
			Root<CMaterialRequestHeaderEntity> cmrRootU = subqueryUser.from(CMaterialRequestHeaderEntity.class);
			Root<User> userRoot = subqueryUser.from(User.class);
			subqueryUser.select(cmrRootU.get("materialRequestId"))
					.where(cb.and(cb.equal(cmrRootU.get("requestedBy"), userRoot.get("bodSeqNo")),
							cb.equal(userRoot.get("mobile"), userMobile)));

			// Subquery 3: MaterialSupplierQuotationUser
			Subquery<String> subqueryMaterial = cq.subquery(String.class);
			Root<CMaterialRequestHeaderEntity> cmrRootM = subqueryMaterial.from(CMaterialRequestHeaderEntity.class);
			Root<MaterialSupplierQuotationUser> msquRoot = subqueryMaterial.from(MaterialSupplierQuotationUser.class);
			subqueryMaterial.select(cmrRootM.get("materialRequestId"))
					.where(cb.and(cb.equal(cmrRootM.get("requestedBy"), msquRoot.get("bodSeqNo")),
							cb.equal(msquRoot.get("mobile"), userMobile)));

			Predicate userMobilePredicate = cb.or(msqhRoot.get("cmatRequestId").in(subqueryCustomer),
					msqhRoot.get("cmatRequestId").in(subqueryUser), msqhRoot.get("cmatRequestId").in(subqueryMaterial));
			predicates.add(userMobilePredicate);
		}

		// ---- Supplier filter ----
		if (supplierId != null && !supplierId.trim().isEmpty()) {
			predicates.add(cb.equal(msqhRoot.get("supplierId"), supplierId));
		}

		// ---- Material Request ID filter ----
		if (cmatRequestId != null && !cmatRequestId.trim().isEmpty()) {
			predicates.add(cb.equal(msqhRoot.get("cmatRequestId"), cmatRequestId));
		}

		// ---- Date filters ----
		if (fromQuotedDate != null && toQuotedDate != null) {
			predicates.add(cb.between(msqhRoot.get("quotedDate"), fromQuotedDate, toQuotedDate));
		} else if (fromQuotedDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(msqhRoot.get("quotedDate"), fromQuotedDate));
		} else if (toQuotedDate != null) {
			predicates.add(cb.lessThanOrEqualTo(msqhRoot.get("quotedDate"), toQuotedDate));
		}

		cq.select(msqhRoot).where(predicates.toArray(new Predicate[0]));

		TypedQuery<MaterialSupplierQuotationHeader> query = entityManager.createQuery(cq);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		List<MaterialSupplierQuotationHeader> results = query.getResultList();

		// --------------------------
		// Count query
		// --------------------------
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialSupplierQuotationHeader> countRoot = countQuery.from(MaterialSupplierQuotationHeader.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (userMobile != null && !userMobile.trim().isEmpty()) {
			// Subquery 1: CustomerRegistration
			Subquery<String> countSubCustomer = countQuery.subquery(String.class);
			Root<CMaterialRequestHeaderEntity> cmrRootCC = countSubCustomer.from(CMaterialRequestHeaderEntity.class);
			Root<CustomerRegistration> crRootC = countSubCustomer.from(CustomerRegistration.class);
			countSubCustomer.select(cmrRootCC.get("materialRequestId"))
					.where(cb.and(cb.equal(cmrRootCC.get("requestedBy"), crRootC.get("userid")),
							cb.equal(crRootC.get("userMobile"), userMobile)));

			// Subquery 2: User
			Subquery<String> countSubUser = countQuery.subquery(String.class);
			Root<CMaterialRequestHeaderEntity> cmrRootUU = countSubUser.from(CMaterialRequestHeaderEntity.class);
			Root<User> userRootC = countSubUser.from(User.class);
			countSubUser.select(cmrRootUU.get("materialRequestId"))
					.where(cb.and(cb.equal(cmrRootUU.get("requestedBy"), userRootC.get("bodSeqNo")),
							cb.equal(userRootC.get("mobile"), userMobile)));

			// Subquery 3: MaterialSupplierQuotationUser
			Subquery<String> countSubMaterial = countQuery.subquery(String.class);
			Root<CMaterialRequestHeaderEntity> cmrRootMM = countSubMaterial.from(CMaterialRequestHeaderEntity.class);
			Root<MaterialSupplierQuotationUser> msquRootC = countSubMaterial.from(MaterialSupplierQuotationUser.class);
			countSubMaterial.select(cmrRootMM.get("materialRequestId"))
					.where(cb.and(cb.equal(cmrRootMM.get("requestedBy"), msquRootC.get("bodSeqNo")),
							cb.equal(msquRootC.get("mobile"), userMobile)));

			countPredicates.add(cb.or(countRoot.get("cmatRequestId").in(countSubCustomer),
					countRoot.get("cmatRequestId").in(countSubUser),
					countRoot.get("cmatRequestId").in(countSubMaterial)));
		}

		if (supplierId != null && !supplierId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("supplierId"), supplierId));
		}
		if (cmatRequestId != null && !cmatRequestId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("cmatRequestId"), cmatRequestId));
		}

		if (fromQuotedDate != null && toQuotedDate != null) {
			countPredicates.add(cb.between(countRoot.get("quotedDate"), fromQuotedDate, toQuotedDate));
		} else if (fromQuotedDate != null) {
			countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("quotedDate"), fromQuotedDate));
		} else if (toQuotedDate != null) {
			countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("quotedDate"), toQuotedDate));
		}

		countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(results, pageable, total);
	}

	@Transactional
	public ResponseInvoiceAndDetailsDto getInvoicesAndDetails(String updatedBy, BigDecimal quotedAmount,
			String cmatRequestId, String invoiceNumber, LocalDate fromInvoiceDate, LocalDate toInvoiceDate,
			Pageable invoicePageable) {

		ResponseInvoiceAndDetailsDto response = new ResponseInvoiceAndDetailsDto();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// ---------- 1️⃣ Fetch Invoices (with pagination) ----------
		CriteriaQuery<Invoice> invoiceQuery = cb.createQuery(Invoice.class);
		Root<Invoice> invoiceRoot = invoiceQuery.from(Invoice.class);

		List<Predicate> invoicePredicates = buildInvoicePredicates(cb, invoiceRoot, updatedBy, quotedAmount,
				cmatRequestId, invoiceNumber, fromInvoiceDate, toInvoiceDate);

		invoiceQuery.select(invoiceRoot).where(cb.and(invoicePredicates.toArray(new Predicate[0])));
		TypedQuery<Invoice> typedInvoiceQuery = entityManager.createQuery(invoiceQuery);

		typedInvoiceQuery.setFirstResult((int) invoicePageable.getOffset());
		typedInvoiceQuery.setMaxResults(invoicePageable.getPageSize());

		List<Invoice> invoices = typedInvoiceQuery.getResultList();

		// Count query for invoices
		CriteriaQuery<Long> countInvoiceQuery = cb.createQuery(Long.class);
		Root<Invoice> countInvoiceRoot = countInvoiceQuery.from(Invoice.class);
		List<Predicate> countInvoicePredicates = buildInvoicePredicates(cb, countInvoiceRoot, updatedBy, quotedAmount,
				cmatRequestId, invoiceNumber, fromInvoiceDate, toInvoiceDate);

		countInvoiceQuery.select(cb.count(countInvoiceRoot))
				.where(cb.and(countInvoicePredicates.toArray(new Predicate[0])));

		Long totalInvoices = entityManager.createQuery(countInvoiceQuery).getSingleResult();
		int invoiceTotalPages = (int) Math.ceil((double) totalInvoices / invoicePageable.getPageSize());
		List<MaterialSupplier> materialSuppliers = new ArrayList<>();
		List<MaterialSupplierQuotationHeader> quotationHeaders = new ArrayList<>();

		if (!invoices.isEmpty()) {
			List<String> invoiceCmatIds = invoices.stream().map(Invoice::getCmatRequestId).toList();

			CriteriaQuery<MaterialSupplier> supplierQuery = cb.createQuery(MaterialSupplier.class);
			Root<MaterialSupplier> supplierRoot = supplierQuery.from(MaterialSupplier.class);
			supplierQuery.select(supplierRoot).where(supplierRoot.get("cmatRequestId").in(invoiceCmatIds));

			materialSuppliers = entityManager.createQuery(supplierQuery).getResultList();
			CriteriaQuery<MaterialSupplierQuotationHeader> quotationQuery = cb
					.createQuery(MaterialSupplierQuotationHeader.class);
			Root<MaterialSupplierQuotationHeader> quotationRoot = quotationQuery
					.from(MaterialSupplierQuotationHeader.class);
			quotationQuery.select(quotationRoot).where(quotationRoot.get("cmatRequestId").in(invoiceCmatIds));

			quotationHeaders = entityManager.createQuery(quotationQuery).getResultList();
		}
		response.setStatus(true);
		response.setMessage("Invoices, Material Suppliers, and Quotation Headers retrieved successfully.");
		response.setInvoices(invoices);
		response.setInvoiceCurrentPage(invoicePageable.getPageNumber());
		response.setInvoicePageSize(invoicePageable.getPageSize());
		response.setInvoiceTotalElements(totalInvoices);
		response.setInvoiceTotalPages(invoiceTotalPages);
		response.setMaterialSuppliers(materialSuppliers);
		response.setMaterialSupplierQuotationHeaders(quotationHeaders);

		return response;
	}

	// ---------- Helper Method ----------
	private List<Predicate> buildInvoicePredicates(CriteriaBuilder cb, Root<Invoice> root, String updatedBy,
			BigDecimal quotedAmount, String cmatRequestId, String invoiceNumber, LocalDate fromInvoiceDate,
			LocalDate toInvoiceDate) {
		List<Predicate> predicates = new ArrayList<>();

		if (updatedBy != null && !updatedBy.isEmpty()) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
		if (quotedAmount != null) {
			predicates.add(cb.equal(root.get("quotedAmount"), quotedAmount));
		}
		if (cmatRequestId != null && !cmatRequestId.isEmpty()) {
			predicates.add(cb.equal(root.get("cmatRequestId"), cmatRequestId));
		}
		if (invoiceNumber != null && !invoiceNumber.isEmpty()) {
			predicates.add(cb.equal(root.get("invoiceNumber"), invoiceNumber));
		}
		if (fromInvoiceDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("invoiceDate"), fromInvoiceDate));
		}
		if (toInvoiceDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("invoiceDate"), toInvoiceDate));
		}
		return predicates;
	}
}
