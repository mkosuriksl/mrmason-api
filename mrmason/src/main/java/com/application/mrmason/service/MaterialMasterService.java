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
			String msCatmsSubCatmsBrandSkuId = userId + "_" + dto.getSku();

			if (materialMasterRepository.existsById(msCatmsSubCatmsBrandSkuId)) {
				throw new RuntimeException(
						"Material already exists for msCatmsSubCatmsBrandSkuId: " + msCatmsSubCatmsBrandSkuId);
			}

			MaterialMaster material = new MaterialMaster();
			material.setUserId(userId);
			material.setMsCatmsSubCatmsBrandSkuId(msCatmsSubCatmsBrandSkuId);
			material.setServiceCategory(dto.getServiceCategory());
			material.setMaterialCategory(dto.getMaterialCategory());
			material.setMaterialSubCategory(dto.getMaterialSubCategory());
			material.setBrand(dto.getBrand());
			material.setModelNo(dto.getModelNo());
			material.setSku(dto.getSku());
			material.setModelName(dto.getModelName());
			material.setDescription(dto.getDescription());
			material.setImage(dto.getImage());
			material.setSize(dto.getSize());
			material.setUpdatedBy(userId);
			material.setShape(dto.getShape());
			material.setWidth(dto.getWidth());
			material.setLength(dto.getLength());
			material.setThickness(dto.getThickness());
			material.setStatus("NEW");
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

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

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
			if (dto.getMsCatmsSubCatmsBrandSkuId() == null || dto.getMsCatmsSubCatmsBrandSkuId().isEmpty()) {
				throw new IllegalArgumentException("userIdSku must be provided for update");
			}

			MaterialMaster material = materialMasterRepository.findById(dto.getMsCatmsSubCatmsBrandSkuId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Material not found for userIdSku: " + dto.getMsCatmsSubCatmsBrandSkuId()));

			material.setServiceCategory(dto.getServiceCategory());
			material.setMaterialCategory(dto.getMaterialCategory());
			material.setMaterialSubCategory(dto.getMaterialSubCategory());
			material.setBrand(dto.getBrand());
			material.setModelNo(dto.getModelNo());
			material.setModelName(dto.getModelName());
			material.setDescription(dto.getDescription());
			material.setImage(dto.getImage());
			material.setSize(dto.getSize());
			material.setUpdatedBy(userId);
			material.setUpdatedDate(LocalDateTime.now());
//			material.setShape(dto.getShape());
//			material.setWidth(dto.getWidth());
//			material.setLength(dto.getLength());
//			material.setThickness(dto.getServiceCategory());
			material.setStatus(dto.getStatus());

			updatedList.add(materialMasterRepository.save(material));
		}

		return updatedList;
	}

	public Page<MaterialMaster> get(String serviceCategory, String materialCategory, String materialSubCategory,
			String brand, String modelNo, String modelName, String msCatmsSubCatmsBrandSkuId, String updatedBy,
			RegSource regSource, Pageable pageable) throws AccessDeniedException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MaterialMaster> query = cb.createQuery(MaterialMaster.class);
		Root<MaterialMaster> root = query.from(MaterialMaster.class);
		List<Predicate> predicates = new ArrayList<>();

		if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("serviceCategory"), serviceCategory));
		}
		if (materialCategory != null && !materialCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("materialCategory"), materialCategory));
		}
		if (materialSubCategory != null && !materialSubCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("materialSubCategory"), materialSubCategory));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (modelNo != null && !modelNo.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelNo"), modelNo));
		}
		if (modelName != null && !modelName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelName"), modelName));
		}
		if (msCatmsSubCatmsBrandSkuId != null && !msCatmsSubCatmsBrandSkuId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("msCatmsSubCatmsBrandSkuId"), msCatmsSubCatmsBrandSkuId));
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
		if (materialCategory != null && !materialCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("materialCategory"), materialCategory));
		}
		if (materialSubCategory != null && !materialSubCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("materialSubCategory"), materialSubCategory));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		}
		if (modelNo != null && !modelNo.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelNo"), modelNo));
		}
		if (modelName != null && !modelName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("modelName"), modelName));
		}
		if (msCatmsSubCatmsBrandSkuId != null && !msCatmsSubCatmsBrandSkuId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("msCatmsSubCatmsBrandSkuId"), msCatmsSubCatmsBrandSkuId));
		}
		if (updatedBy != null && !updatedBy.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

}
