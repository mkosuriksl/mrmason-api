package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.MaterialMasterRequestDto;
import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.MaterialMasterRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.security.AuthDetailsProvider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class MaterialMasterService {

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;
    
	@PersistenceContext
	private EntityManager entityManager;

    // Save multiple materials
    @Transactional
    public List<MaterialMaster> saveMaterials(List<MaterialMasterRequestDto> dtos, RegSource regSource) {
        UserInfo userInfo = getLoggedInUserInfo(regSource);
        String userId = userInfo.userId;

        List<MaterialMaster> materials = new ArrayList<>();

        for (MaterialMasterRequestDto dto : dtos) {
            String userIdSku = userId + "_" + dto.getSku();

            if (materialMasterRepository.existsById(userIdSku)) {
                throw new RuntimeException("Material already exists for userId_skuId: " + userIdSku);
            }

            MaterialMaster material = new MaterialMaster();
            material.setUserId(userId);
            material.setUserIdSku(userIdSku);
            material.setServiceCategory(dto.getServiceCategory());
            material.setProductCategory(dto.getProductCategory());
            material.setProductSubCategory(dto.getProductSubCategory());
            material.setBrand(dto.getBrand());
            material.setModel(dto.getModel());
            material.setSku(dto.getSku());
            material.setName(dto.getName());
            material.setDescription(dto.getDescription());
            material.setImage(dto.getImage());
            material.setSize(dto.getSize());
            material.setUpdatedBy(userId);
            material.setUpdatedDate(LocalDateTime.now());

            materials.add(material);
        }

        return materialMasterRepository.saveAll(materials);
    }

    // Keep your existing UserInfo class
    private static class UserInfo {
        String userId;

        UserInfo(String userId) {
            this.userId = userId;
        }
    }

    private UserInfo getLoggedInUserInfo(RegSource regSource) {
        String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
        Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

        List<String> roleNames = loggedInRole.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        if (!roleNames.contains("MS")) {
            throw new ResourceNotFoundException(
                "Only MaterialSupplierQuotation(MS) role is allowed. Found roles: " + roleNames);
        }

        UserType userType = UserType.MS;

        MaterialSupplierQuotationUser ms = materialSupplierQuotationUserDAO
                .findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
                .orElseThrow(() -> new ResourceNotFoundException("MS not found: " + loggedInUserEmail));

        String userId = ms.getBodSeqNo();
        return new UserInfo(userId);
    }
    
    @Transactional
    public List<MaterialMaster> updateMaterials(List<MaterialMasterRequestDto> dtos, RegSource regSource) {
        UserInfo userInfo = getLoggedInUserInfo(regSource);
        String userId = userInfo.userId;

        List<MaterialMaster> updatedList = new ArrayList<>();

        for (MaterialMasterRequestDto dto : dtos) {
            if (dto.getUserIdSku() == null || dto.getUserIdSku().isEmpty()) {
                throw new IllegalArgumentException("userIdSku must be provided for update");
            }

            MaterialMaster material = materialMasterRepository.findById(dto.getUserIdSku())
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found for userIdSku: " + dto.getUserIdSku()));

            material.setServiceCategory(dto.getServiceCategory());
            material.setProductCategory(dto.getProductCategory());
            material.setProductSubCategory(dto.getProductSubCategory());
            material.setBrand(dto.getBrand());
            material.setModel(dto.getModel());
            material.setName(dto.getName());
            material.setDescription(dto.getDescription());
            material.setImage(dto.getImage());
            material.setSize(dto.getSize());
            material.setUpdatedBy(userId);
            material.setUpdatedDate(LocalDateTime.now());

            updatedList.add(materialMasterRepository.save(material));
        }

        return updatedList;
    }

	public Page<MaterialMaster> get(String serviceCategory, String productCategory, String productSubCategory,
			String brand,String model,String userIdSku,String updatedBy, RegSource regSource, Pageable pageable) throws AccessDeniedException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MaterialMaster> query = cb.createQuery(MaterialMaster.class);
		Root<MaterialMaster> root = query.from(MaterialMaster.class);
		List<Predicate> predicates = new ArrayList<>();

		if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("serviceCategory"), serviceCategory));
		}
		if (productCategory != null && !productCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("productCategory"), productCategory));
		}
		if (productSubCategory != null && !productSubCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("productSubCategory"), productSubCategory));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (model != null && !model.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("model"), model));
		}
		if (userIdSku != null && !userIdSku.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userIdSku"), userIdSku));
		}
		if (updatedBy != null && !updatedBy.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<MaterialMaster> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialMaster> countRoot = countQuery.from(MaterialMaster.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("serviceCategory"), serviceCategory));
		}
		if (productCategory != null && !productCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("productCategory"), productCategory));
		}
		if (productSubCategory != null && !productSubCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("productSubCategory"), productSubCategory));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		}
		if (model != null && !model.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("model"), model));
		}
		if (userIdSku != null && !userIdSku.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userIdSku"), userIdSku));
		}
		if (updatedBy != null && !updatedBy.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

}
