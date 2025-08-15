package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.enums.Status;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CMaterialReqHeaderDetailsRepository;
import com.application.mrmason.repository.MaterialSupplierRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
@Service
public class materialSupplierService {

	@Autowired
	public AdminDetailsRepo adminRepo;
	@Autowired
	UserDAO userDAO;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private MaterialSupplierRepository materialSupplierRepository;
	@Autowired
	private CMaterialReqHeaderDetailsRepository cMaterialReqHeaderDetailsRepository;

	@Transactional
	public GenericResponse<List<MaterialSupplier>> saveItems(
	        List<MaterialSupplier> materialQuotation, RegSource regSource) {
	    UserInfo userInfo = getLoggedInUserInfo(regSource);
	    List<MaterialSupplier> validatedItems = new ArrayList<>();
	    Set<String> seenLineItems = new HashSet<>();
	    for (MaterialSupplier item : materialQuotation) {
	        item.setQuotationId(null);
	        if (!seenLineItems.add(item.getMaterialLineItem())) {
	            throw new IllegalArgumentException(
	                "Duplicate materialLineItem found in request: " + item.getMaterialLineItem()
	            );
	        }
	        boolean exists = materialSupplierRepository
	                .existsBySupplierIdAndMaterialLineItem(userInfo.userId, item.getMaterialLineItem());
	        if (exists) {
	            throw new IllegalArgumentException(
	                "MaterialLineItem already exists for this supplier: " + item.getMaterialLineItem()
	            );
	        }
	        Optional<CMaterialReqHeaderDetailsEntity> materialReqOpt =
	                cMaterialReqHeaderDetailsRepository.findById(item.getMaterialLineItem());
	        if (materialReqOpt.isEmpty()) {
	            throw new ResourceNotFoundException(
	                "MaterialLineItem not found in CMaterialReqHeaderDetailsEntity: " + item.getMaterialLineItem()
	            );
	        }
	        item.setUpdatedDate(LocalDate.now());
	        item.setSupplierId(userInfo.userId);
	        item.setStatus(Status.QUOTED);
	        item.setQuotedDate(LocalDate.now());
	        validatedItems.add(item);
	    }
	    List<MaterialSupplier> saved = materialSupplierRepository.saveAll(validatedItems);
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
		if (!roleNames.contains("Developer")) {
			throw new ResourceNotFoundException("Only Developer role is allowed. Found roles: " + roleNames);
		}
		UserType userType = UserType.Developer;
		User developer = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
				.orElseThrow(() -> new ResourceNotFoundException("Developer not found: " + loggedInUserEmail));
		String userId = developer.getBodSeqNo();
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

	            updatedTasks.add(existingTask);
	        }

	        return materialSupplierRepository.saveAll(updatedTasks);
	    }

}
