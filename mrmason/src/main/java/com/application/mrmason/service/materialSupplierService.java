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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.entity.CMaterialReqHeaderDetailsEntity;
import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.entity.MaterialSupplierQuotationHeader;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
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

}
