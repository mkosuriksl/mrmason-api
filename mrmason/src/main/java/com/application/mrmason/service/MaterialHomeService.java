package com.application.mrmason.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.MaterialSupplierDto;
import com.application.mrmason.dto.ResponseGetAssetsDto;
import com.application.mrmason.dto.ResponseGetMasterDto;
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.entity.MaterialPricing;
import com.application.mrmason.entity.MaterialSupplierAssets;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.UploadMatericalMasterImages;
import com.application.mrmason.repository.MaterialMasterRepository;
import com.application.mrmason.repository.MaterialPricingRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.UploadMatericalMasterImagesRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class MaterialHomeService {

	@Autowired
	private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;

	@Autowired
	private MaterialMasterRepository masterRepo;

	@Autowired
	private MaterialPricingRepository pricingRepo;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UploadMatericalMasterImagesRepository uploadMMRepo;

	public ResponseGetMasterDto getMaterialsWithPagination(String location, String materialCategory,
			String materialSubCategory, String brand, String modelName, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		// 1. Get suppliers by location
		List<MaterialSupplierQuotationUser> supplierEntities = materialSupplierQuotationUserDAO
				.findByLocationContaining(location);

		// Map to DTO
		List<MaterialSupplierDto> suppliers = supplierEntities.stream().map(s -> {
			MaterialSupplierDto dto = new MaterialSupplierDto();
			dto.setBodSeqNo(s.getBodSeqNo());
			dto.setName(s.getName());
			dto.setBusinessName(s.getBusinessName());
			dto.setMobile(s.getMobile());
			dto.setEmail(s.getEmail());
			dto.setAddress(s.getAddress());
			dto.setCity(s.getCity());
			dto.setDistrict(s.getDistrict());
			dto.setState(s.getState());
			dto.setLocation(s.getLocation());
			return dto;
		}).toList();

		if (suppliers.isEmpty()) {
			ResponseGetMasterDto emptyResponse = new ResponseGetMasterDto();
			emptyResponse.setMessage("No suppliers found for location: " + location);
			emptyResponse.setStatus(false);
			emptyResponse.setMaterialMaster(Collections.emptyList());
			emptyResponse.setMaterialSupplier(Collections.emptyList());
			emptyResponse.setMasterPricing(Collections.emptyList());
			emptyResponse.setCurrentPage(page);
			emptyResponse.setPageSize(size);
			emptyResponse.setTotalElements(0);
			emptyResponse.setTotalPages(0);
			return emptyResponse;
		}

		List<String> userIds = supplierEntities.stream().map(MaterialSupplierQuotationUser::getBodSeqNo).toList();

		// 2. Fetch MaterialMaster using CriteriaBuilder with pagination
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MaterialMaster> query = cb.createQuery(MaterialMaster.class);
		Root<MaterialMaster> root = query.from(MaterialMaster.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(root.get("updatedBy").in(userIds));

		if (materialCategory != null && !materialCategory.isEmpty())
			predicates.add(cb.equal(root.get("materialCategory"), materialCategory));
		if (materialSubCategory != null && !materialSubCategory.isEmpty())
			predicates.add(cb.equal(root.get("materialSubCategory"), materialSubCategory));
		if (brand != null && !brand.isEmpty())
			predicates.add(cb.equal(root.get("brand"), brand));
		if (modelName != null && !modelName.isEmpty())
			predicates.add(cb.equal(root.get("modelName"), modelName));

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<MaterialMaster> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());
		List<MaterialMaster> masterList = typedQuery.getResultList();

		// 3. Count query for pagination
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialMaster> countRoot = countQuery.from(MaterialMaster.class);
		List<Predicate> countPredicates = new ArrayList<>();
		countPredicates.add(countRoot.get("updatedBy").in(userIds));

		if (materialCategory != null && !materialCategory.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("materialCategory"), materialCategory));
		if (materialSubCategory != null && !materialSubCategory.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("materialSubCategory"), materialSubCategory));
		if (brand != null && !brand.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		if (modelName != null && !modelName.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("modelName"), modelName));

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		// 4. Fetch pricing for current page SKUs
		List<String> skuList = masterList.stream().map(MaterialMaster::getMsCatmsSubCatmsBrandSkuId).toList();
		List<MaterialPricing> pricingList = pricingRepo.findByUserIdSkuIn(skuList);

		List<UploadMatericalMasterImages> imageList = uploadMMRepo.findBySkuIdIn(skuList);

	    // 6. Map SKU → Image Entity
	    Map<String, UploadMatericalMasterImages> imageMap = imageList.stream()
	            .collect(Collectors.toMap(UploadMatericalMasterImages::getSkuId, img -> img));

	    // 7. Set image data in MaterialMaster transient fields
	    for (MaterialMaster material : masterList) {
	        UploadMatericalMasterImages img = imageMap.get(material.getMsCatmsSubCatmsBrandSkuId());
	        if (img != null) {
	            material.setMaterialMasterImage1(img.getMaterialMasterImage1());
	            material.setMaterialMasterImage2(img.getMaterialMasterImage2());
	            material.setMaterialMasterImage3(img.getMaterialMasterImage3());
	            material.setMaterialMasterImage4(img.getMaterialMasterImage4());
	            material.setMaterialMasterImage5(img.getMaterialMasterImage5());
	        }
	    }

		// 5. Build Response DTO
		ResponseGetMasterDto responseDto = new ResponseGetMasterDto();
		responseDto.setMessage("Material Master is retrieved successfully.");
		responseDto.setStatus(true);
		responseDto.setMaterialMaster(masterList);
		responseDto.setMaterialSupplier(suppliers);
		responseDto.setMasterPricing(pricingList);
		responseDto.setCurrentPage(page);
		responseDto.setPageSize(size);
		responseDto.setTotalElements(totalElements);
		responseDto.setTotalPages((int) Math.ceil((double) totalElements / size));

		return responseDto;
	}

	public List<String> autoSearchLocations(String locationPrefix, String materialCategory, String materialSubCategory,
			String brand, String model) {
		String safeInput = (locationPrefix == null) ? "" : locationPrefix.trim();

// Base search — all matching locations
		List<String> locations = materialSupplierQuotationUserDAO.findDistinctLocationsByPrefix(safeInput);

// Optional filters (category, brand, etc.) — only if you want to filter using AdminMaterialMaster
		if ((materialCategory != null && !materialCategory.isEmpty())
				|| (materialSubCategory != null && !materialSubCategory.isEmpty())
				|| (brand != null && !brand.isEmpty()) || (model != null && !model.isEmpty())) {

// Build query to narrow down suppliers based on material filters
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> query = cb.createQuery(String.class);
			Root<AdminMaterialMaster> root = query.from(AdminMaterialMaster.class);

			List<Predicate> predicates = new ArrayList<>();

			if (materialCategory != null && !materialCategory.isEmpty())
				predicates.add(cb.equal(root.get("materialCategory"), materialCategory));
			if (materialSubCategory != null && !materialSubCategory.isEmpty())
				predicates.add(cb.equal(root.get("materialSubCategory"), materialSubCategory));
			if (brand != null && !brand.isEmpty())
				predicates.add(cb.equal(root.get("brand"), brand));
			if (model != null && !model.isEmpty())
				predicates.add(cb.equal(root.get("modelName"), model));

			query.select(root.get("updatedBy")).where(cb.and(predicates.toArray(new Predicate[0])));
			List<String> supplierIds = entityManager.createQuery(query).getResultList();

// Filter locations belonging to these suppliers
			locations = materialSupplierQuotationUserDAO
					.findDistinctByBodSeqNoInAndLocationStartingWithIgnoreCase(supplierIds, safeInput);
		}

		return locations;
	}

	public ResponseGetAssetsDto getAssetsWithPagination(String assetSubCat, String assetBrand, String assetModel,String assetCat, String location, 
//			String userId, 
			int page, int size) {

		Pageable pageable = PageRequest.of(page, size);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// ✅ Step 1: Build main query
		CriteriaQuery<MaterialSupplierAssets> query = cb.createQuery(MaterialSupplierAssets.class);
		Root<MaterialSupplierAssets> root = query.from(MaterialSupplierAssets.class);

		List<Predicate> predicates = new ArrayList<>();

		if (assetSubCat != null && !assetSubCat.isEmpty())
			predicates.add(cb.equal(root.get("assetSubCat"), assetSubCat));

		if (assetBrand != null && !assetBrand.isEmpty())
			predicates.add(cb.equal(root.get("assetBrand"), assetBrand));

		if (assetModel != null && !assetModel.isEmpty())
			predicates.add(cb.equal(root.get("assetModel"), assetModel));

		if (assetCat != null && !assetCat.isEmpty())
			predicates.add(cb.equal(root.get("assetCat"), assetCat));

		if (location != null && !location.trim().isEmpty())
			predicates.add(cb.like(cb.lower(root.get("location")), location.trim().toLowerCase() + "%"));

//		if (userId != null && !userId.trim().isEmpty())
//			predicates.add(cb.equal(root.get("userId"), userId.trim()));
		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

		// ✅ Step 2: Pagination
		TypedQuery<MaterialSupplierAssets> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());
		List<MaterialSupplierAssets> assets = typedQuery.getResultList();

		// ✅ Step 3: Count total elements
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialSupplierAssets> countRoot = countQuery.from(MaterialSupplierAssets.class);

		List<Predicate> countPredicates = new ArrayList<>();

		if (assetSubCat != null && !assetSubCat.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetSubCat"), assetSubCat));

		if (assetBrand != null && !assetBrand.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetBrand"), assetBrand));

		if (assetModel != null && !assetModel.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetModel"), assetModel));

		if (assetCat != null && !assetCat.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetCat"), assetCat));

		if (location != null && !location.trim().isEmpty())
			countPredicates.add(cb.like(cb.lower(countRoot.get("location")), location.trim().toLowerCase() + "%"));

//		if (userId != null && !userId.trim().isEmpty())
//			countPredicates.add(cb.equal(countRoot.get("userId"), userId.trim()));
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		// ✅ Step 4: Get supplier IDs
		List<String> userIds = assets.stream().map(MaterialSupplierAssets::getUserId).filter(Objects::nonNull)
				.distinct().toList();

		// ✅ Step 5: Fetch supplier details
		List<MaterialSupplierQuotationUser> supplierEntities = userIds.isEmpty() ? Collections.emptyList()
				: materialSupplierQuotationUserDAO.findAllById(userIds);

		List<MaterialSupplierDto> suppliers = supplierEntities.stream().map(s -> {
			MaterialSupplierDto dto = new MaterialSupplierDto();
			dto.setBodSeqNo(s.getBodSeqNo());
			dto.setName(s.getName());
			dto.setBusinessName(s.getBusinessName());
			dto.setMobile(s.getMobile());
			dto.setEmail(s.getEmail());
			dto.setAddress(s.getAddress());
			dto.setCity(s.getCity());
			dto.setDistrict(s.getDistrict());
			dto.setState(s.getState());
			dto.setLocation(s.getLocation());
			return dto;
		}).toList();

		// ✅ Step 6: Prepare Response DTO
		ResponseGetAssetsDto responseDto = new ResponseGetAssetsDto();
		responseDto.setMessage("Assets retrieved successfully");
		responseDto.setStatus(true);
		responseDto.setAssets(assets);
		responseDto.setSuppliers(suppliers);
		responseDto.setCurrentPage(page);
		responseDto.setPageSize(size);
		responseDto.setTotalElements(totalElements);
		responseDto.setTotalPages((int) Math.ceil((double) totalElements / size));

		return responseDto;
	}

}
