package com.application.mrmason.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.CMaterialReqHeaderDetailsEntity;
import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.entity.MaterialSupplierQuotationHeader;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.enums.Status;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CMaterialReqHeaderDetailsRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationHeaderRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.MaterialSupplierRepository;
import com.application.mrmason.security.AuthDetailsProvider;

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

//	@Transactional
//	public GenericResponse<List<MaterialSupplier>> saveItems(
//	        List<MaterialSupplier> materialQuotation, RegSource regSource) {
//	    UserInfo userInfo = getLoggedInUserInfo(regSource);
//	    List<MaterialSupplier> validatedItems = new ArrayList<>();
//	    Set<String> seenLineItems = new HashSet<>();
//	    for (MaterialSupplier item : materialQuotation) {
//	        item.setQuotationId(null);
//	        if (!seenLineItems.add(item.getMaterialLineItem())) {
//	            throw new IllegalArgumentException(
//	                "Duplicate materialLineItem found in request: " + item.getMaterialLineItem()
//	            );
//	        }
//	        boolean exists = materialSupplierRepository
//	                .existsBySupplierIdAndMaterialLineItem(userInfo.userId, item.getMaterialLineItem());
//	        if (exists) {
//	            throw new IllegalArgumentException(
//	                "MaterialLineItem already exists for this supplier: " + item.getMaterialLineItem()
//	            );
//	        }
//	        Optional<CMaterialReqHeaderDetailsEntity> materialReqOpt =
//	                cMaterialReqHeaderDetailsRepository.findById(item.getMaterialLineItem());
//	        if (materialReqOpt.isEmpty()) {
//	            throw new ResourceNotFoundException(
//	                "MaterialLineItem not found in CMaterialReqHeaderDetailsEntity: " + item.getMaterialLineItem()
//	            );
//	        }
//	        item.setUpdatedDate(LocalDate.now());
//	        item.setSupplierId(userInfo.userId);
//	        item.setStatus(Status.QUOTED);
//	        item.setQuotedDate(LocalDate.now());
//	        validatedItems.add(item);
//	    }
//	    List<MaterialSupplier> saved = materialSupplierRepository.saveAll(validatedItems);
//	    return new GenericResponse<>(
//	        "Material Quotations saved successfully by user: " + userInfo.userId,
//	        true,
//	        saved
//	    );
//	}
	@Transactional
	public GenericResponse<List<MaterialSupplier>> saveItems(
	        List<MaterialSupplier> materialQuotation,
	        String cMatRequestId,String invoiceNumber,Status invoiceStatus,Status quotationStatus,LocalDate invoiceDate,
	        RegSource regSource) {

	    UserInfo userInfo = getLoggedInUserInfo(regSource);

	    // ✅ Validate by Optional
	    Optional<CMaterialReqHeaderDetailsEntity> headerOpt =
	            cMaterialReqHeaderDetailsRepository.findFirstByCMatRequestId(cMatRequestId);

	    if (headerOpt.isEmpty()) {
	        throw new ResourceNotFoundException(
	                "cMatRequestId not found in CMaterialReqHeaderDetailsEntity: " + cMatRequestId);
	    }

	    List<MaterialSupplier> validatedItems = new ArrayList<>();
	    Set<String> seenLineItems = new HashSet<>();

	    for (MaterialSupplier item : materialQuotation) {

	        // Validate that materialLineItem starts with cMatRequestId + "_"
	        if (!item.getMaterialLineItem().startsWith(cMatRequestId + "_")) {
	            throw new IllegalArgumentException(
	                "MaterialLineItem '" + item.getMaterialLineItem() +
	                "' does not belong to cMatRequestId '" + cMatRequestId + "'");
	        }

	        item.setQuotationId(null);
	        item.setCmatRequestId(cMatRequestId);

	        if (!seenLineItems.add(item.getMaterialLineItem())) {
	            throw new IllegalArgumentException(
	                "Duplicate materialLineItem found in request: " + item.getMaterialLineItem());
	        }

	        boolean exists = materialSupplierRepository
	                .existsBySupplierIdAndMaterialLineItem(userInfo.userId, item.getMaterialLineItem());
	        if (exists) {
	            throw new IllegalArgumentException(
	                "MaterialLineItem already exists for this supplier: " + item.getMaterialLineItem());
	        }

	        Optional<CMaterialReqHeaderDetailsEntity> materialReqOpt =
	                cMaterialReqHeaderDetailsRepository.findById(item.getMaterialLineItem());
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

	    // ✅ Calculate total quoted amount (sum of quotedAmount)
	    BigDecimal totalQuotedAmount = saved.stream()
	            .map(MaterialSupplier::getQuotedAmount)
	            .filter(Objects::nonNull)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	    // ✅ Save header record
	    MaterialSupplierQuotationHeader quotationHeader = new MaterialSupplierQuotationHeader();
	    quotationHeader.setCmatRequestId(cMatRequestId);
	    quotationHeader.setInvoiceNumber(invoiceNumber);
	    quotationHeader.setInvoiceStatus(invoiceStatus);
	    quotationHeader.setInvoiceDate(LocalDate.now());
	    quotationHeader.setQuotationStatus(quotationStatus);
	    
	    quotationHeader.setQuotationId(saved.get(0).getQuotationId()); // pick any from saved, or generate
	    quotationHeader.setQuotedAmount(totalQuotedAmount);
	    quotationHeader.setSupplierId(userInfo.userId);
	    quotationHeader.setQuotedDate(LocalDate.now());
	    quotationHeader.setUpdatedDate(LocalDate.now());
	    
	    
	    materialSupplierQuotationHeaderRepository.save(quotationHeader);

	    return new GenericResponse<>(
	            "Material Quotations saved successfully by user: " + userInfo.userId,
	            true,
	            saved
	    );
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
			throw new ResourceNotFoundException("Only MaterialSupplierQuotation(MS) role is allowed. Found roles: " + roleNames);
		}
		UserType userType = UserType.MS;
		MaterialSupplierQuotationUser ms = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
				.orElseThrow(() -> new ResourceNotFoundException("MS not found: " + loggedInUserEmail));
		String userId = ms.getBodSeqNo();
		return new UserInfo(userId);
	}

	    public List<MaterialSupplier> updateMaterial(RegSource regSource, List<MaterialSupplier> taskList) throws AccessDeniedException {
	    	 UserInfo userInfo = getLoggedInUserInfo(regSource);
	        List<MaterialSupplier> updatedTasks = new ArrayList<>();

	        for (MaterialSupplier task : taskList) {
	            String taskIdKey = task.getMaterialLineItem();

	            if (taskIdKey == null || taskIdKey.isEmpty()) {
	                throw new ResourceNotFoundException("material line item is required for update.");
	            }

	            // Fetch the existing entity
	            MaterialSupplier existingTask = materialSupplierRepository.findById(taskIdKey)
	                    .orElseThrow(() -> new ResourceNotFoundException("Material Line Item not found for MaterialSupplier: " + taskIdKey));

	            // Only update allowed fields
	            existingTask.setDiscount(task.getDiscount());
	            existingTask.setSupplierId(userInfo.userId);
	            existingTask.setUpdatedDate(LocalDate.now());
	            existingTask.setStatus(task.getStatus());
	            existingTask.setGst(task.getGst());
	            existingTask.setMrp(task.getMrp());
	            updatedTasks.add(existingTask);
	        }

	        return materialSupplierRepository.saveAll(updatedTasks);
	    }

	    public Page<MaterialSupplier> getMaterialSupplierDetails(
				String quotationId,Pageable pageable) {

		    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		    // === Main query ===
		    CriteriaQuery<MaterialSupplier> query = cb.createQuery(MaterialSupplier.class);
		    Root<MaterialSupplier> root = query.from(MaterialSupplier.class);
		    List<Predicate> predicates = new ArrayList<>();

		    if (quotationId != null && !quotationId.trim().isEmpty()) {
		        predicates.add(cb.equal(root.get("quotationId"), quotationId));
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
		   
		    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		    Long total = entityManager.createQuery(countQuery).getSingleResult();

		    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	    }
	    
	    public Page<MaterialSupplierQuotationHeader> getQuotationsByUserMobile(
	            String userMobile,
	            String supplierId,
	            LocalDate fromQuotedDate,
	            LocalDate toQuotedDate,
	            Pageable pageable) {

	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	        // --- Main query ---
	        CriteriaQuery<MaterialSupplierQuotationHeader> cq = cb.createQuery(MaterialSupplierQuotationHeader.class);
	        Root<MaterialSupplierQuotationHeader> msqhRoot = cq.from(MaterialSupplierQuotationHeader.class);

	        List<Predicate> predicates = new ArrayList<>();

	        // If userMobile is provided → join via subqueries (both tables)
	        if (userMobile != null && !userMobile.trim().isEmpty()) {

	            // Subquery from CustomerRegistration
	            Subquery<String> subqueryCustomer = cq.subquery(String.class);
	            Root<CMaterialRequestHeaderEntity> cmrRootC = subqueryCustomer.from(CMaterialRequestHeaderEntity.class);
	            Root<CustomerRegistration> crRoot = subqueryCustomer.from(CustomerRegistration.class);

	            subqueryCustomer.select(cmrRootC.get("materialRequestId"))
	                    .where(
	                            cb.and(
	                                    cb.equal(cmrRootC.get("requestedBy"), crRoot.get("userid")),
	                                    cb.equal(crRoot.get("userMobile"), userMobile)
	                            )
	                    );

	            // Subquery from User table
	            Subquery<String> subqueryUser = cq.subquery(String.class);
	            Root<CMaterialRequestHeaderEntity> cmrRootU = subqueryUser.from(CMaterialRequestHeaderEntity.class);
	            Root<User> userRoot = subqueryUser.from(User.class);

	            subqueryUser.select(cmrRootU.get("materialRequestId"))
	                    .where(
	                            cb.and(
	                                    cb.equal(cmrRootU.get("requestedBy"), userRoot.get("bodSeqNo")),
	                                    cb.equal(userRoot.get("mobile"), userMobile)
	                            )
	                    );

	            // Combine with OR
	            Predicate userMobilePredicate = cb.or(
	                    msqhRoot.get("cmatRequestId").in(subqueryCustomer),
	                    msqhRoot.get("cmatRequestId").in(subqueryUser)
	            );

	            predicates.add(userMobilePredicate);
	        }

	        // Supplier filter
	        if (supplierId != null && !supplierId.trim().isEmpty()) {
	            predicates.add(cb.equal(msqhRoot.get("supplierId"), supplierId));
	        }

	        // Date range filters
	        if (fromQuotedDate != null && toQuotedDate != null) {
	            predicates.add(cb.between(msqhRoot.get("quotedDate"), fromQuotedDate, toQuotedDate));
	        } else if (fromQuotedDate != null) {
	            predicates.add(cb.greaterThanOrEqualTo(msqhRoot.get("quotedDate"), fromQuotedDate));
	        } else if (toQuotedDate != null) {
	            predicates.add(cb.lessThanOrEqualTo(msqhRoot.get("quotedDate"), toQuotedDate));
	        }

	        cq.select(msqhRoot);
	        if (!predicates.isEmpty()) {
	            cq.where(cb.and(predicates.toArray(new Predicate[0])));
	        }

	        TypedQuery<MaterialSupplierQuotationHeader> query = entityManager.createQuery(cq);
	        query.setFirstResult((int) pageable.getOffset());
	        query.setMaxResults(pageable.getPageSize());
	        List<MaterialSupplierQuotationHeader> results = query.getResultList();

	        // --- Count query ---
	        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	        Root<MaterialSupplierQuotationHeader> countRoot = countQuery.from(MaterialSupplierQuotationHeader.class);

	        List<Predicate> countPredicates = new ArrayList<>();

	        if (userMobile != null && !userMobile.trim().isEmpty()) {

	            // Count subquery (Customer)
	            Subquery<String> countSubqueryCustomer = countQuery.subquery(String.class);
	            Root<CMaterialRequestHeaderEntity> cmrRootCountC = countSubqueryCustomer.from(CMaterialRequestHeaderEntity.class);
	            Root<CustomerRegistration> crRootCount = countSubqueryCustomer.from(CustomerRegistration.class);

	            countSubqueryCustomer.select(cmrRootCountC.get("materialRequestId"))
	                    .where(
	                            cb.and(
	                                    cb.equal(cmrRootCountC.get("requestedBy"), crRootCount.get("userid")),
	                                    cb.equal(crRootCount.get("userMobile"), userMobile)
	                            )
	                    );

	            // Count subquery (User)
	            Subquery<String> countSubqueryUser = countQuery.subquery(String.class);
	            Root<CMaterialRequestHeaderEntity> cmrRootCountU = countSubqueryUser.from(CMaterialRequestHeaderEntity.class);
	            Root<User> userRootCount = countSubqueryUser.from(User.class);

	            countSubqueryUser.select(cmrRootCountU.get("materialRequestId"))
	                    .where(
	                            cb.and(
	                                    cb.equal(cmrRootCountU.get("requestedBy"), userRootCount.get("bodSeqNo")),
	                                    cb.equal(userRootCount.get("mobile"), userMobile)
	                            )
	                    );

	            countPredicates.add(
	                    cb.or(
	                            countRoot.get("cmatRequestId").in(countSubqueryCustomer),
	                            countRoot.get("cmatRequestId").in(countSubqueryUser)
	                    )
	            );
	        }

	        // Supplier filter
	        if (supplierId != null && !supplierId.trim().isEmpty()) {
	            countPredicates.add(cb.equal(countRoot.get("supplierId"), supplierId));
	        }

	        // Date range filters for count query
	        if (fromQuotedDate != null && toQuotedDate != null) {
	            countPredicates.add(cb.between(countRoot.get("quotedDate"), fromQuotedDate, toQuotedDate));
	        } else if (fromQuotedDate != null) {
	            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("quotedDate"), fromQuotedDate));
	        } else if (toQuotedDate != null) {
	            countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("quotedDate"), toQuotedDate));
	        }

	        countQuery.select(cb.count(countRoot));
	        if (!countPredicates.isEmpty()) {
	            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
	        }

	        Long total = entityManager.createQuery(countQuery).getSingleResult();

	        return new PageImpl<>(results, pageable, total);
	    }
}
