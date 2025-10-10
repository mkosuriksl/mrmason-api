package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.config.AWSConfig;
import com.application.mrmason.dto.AdminDetailsDto;
import com.application.mrmason.dto.AdminMaterialMasterResponseDTO;
import com.application.mrmason.dto.AdminMaterialMasterResponseWithImageDto;
import com.application.mrmason.dto.MaterialDTO;
import com.application.mrmason.dto.MaterialGroupDTO;
import com.application.mrmason.dto.MaterialSupplierDto;
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.UploadMatericalMasterImages;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminMaterialMasterRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.UploadMatericalMasterImagesRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.AdminMaterialMasterService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class AdminMaterialMasterServiceImpl implements AdminMaterialMasterService {

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	private AdminMaterialMasterRepository adminMaterialMasterRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AWSConfig awsConfig;

	@Autowired
	private UploadMatericalMasterImagesRepository uploadMatericalMasterImagesRepository;

	@Autowired
	private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;

	@Override
	public List<MaterialGroupDTO> createAdminMaterialMaster(List<MaterialGroupDTO> requestGroups, RegSource regSource)
			throws AccessDeniedException {

		UserInfo userInfo = getLoggedInUserInfo(regSource);

		// 1. Flatten materials and generate SKU
		List<AdminMaterialMaster> entitiesToSave = new ArrayList<>();
		for (MaterialGroupDTO group : requestGroups) {
			for (MaterialDTO m : group.getMaterials()) {
				if (m.getSkuId() == null) {

					String skuId = userInfo.userId + "_" + m.getSkuId();
					m.setSkuId(skuId);
				}
				// Create entity
				AdminMaterialMaster entity = new AdminMaterialMaster();
				entity.setSkuId(userInfo.userId + "_" + m.getSkuId());
				entity.setMaterialCategory(group.getMaterialCategory());
				entity.setMaterialSubCategory(group.getMaterialSubCategory());
				entity.setBrand(group.getBrand());
				entity.setModelNo(m.getModelNo());
				entity.setModelName(m.getModelName());
				entity.setShape(m.getShape());
				entity.setWidth(m.getWidth());
				entity.setLength(m.getLength());
				entity.setSize(m.getSize());
				entity.setThickness(m.getThickness());
				entity.setUpdatedBy(userInfo.userId);
				entity.setUpdatedDate(new Date());
				entity.setStatus("Active");

				entitiesToSave.add(entity);
			}
		}

		// 2. Save all materials
		adminMaterialMasterRepository.saveAll(entitiesToSave);

		// 3. Return same grouped structure
		return requestGroups;
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

		if (roleNames.equals("MS")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));
		String userId;

		if (userType == UserType.Adm) {
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getEmail(); // or any other logic you want
		} else {
			MaterialSupplierQuotationUser user = materialSupplierQuotationUserDAO
					.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("Material User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}

		return new UserInfo(userId);
	}

	@Override
	public List<AdminMaterialMaster> updateAdminMaterialMasters(List<AdminMaterialMaster> updatedList,
			RegSource regSource) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInUserInfo(regSource);

		List<AdminMaterialMaster> savedMaterials = updatedList.stream().map(material -> {
			Optional<AdminMaterialMaster> existingOpt = adminMaterialMasterRepository.findBySkuId(material.getSkuId());

			if (existingOpt.isPresent()) {
				AdminMaterialMaster existing = existingOpt.get();

				// Do NOT allow updating these fields:
				material.setBrand(existing.getBrand());
				material.setMaterialCategory(existing.getMaterialCategory());
				material.setMaterialSubCategory(existing.getMaterialSubCategory());

				// Allow updating these fields:
				material.setUpdatedBy(userInfo.userId);
				material.setUpdatedDate(new Date());
				material.setLength(updatedList.get(0).getLength());
				material.setModelName(updatedList.get(0).getModelName());
				material.setShape(updatedList.get(0).getShape());
				material.setWidth(updatedList.get(0).getWidth());
				material.setSize(updatedList.get(0).getSize());
				material.setThickness(updatedList.get(0).getThickness());
				material.setStatus(updatedList.get(0).getStatus());
				return material;
			} else {
				throw new RuntimeException("Material with SKU ID " + material.getSkuId() + " not found.");
			}
		}).collect(Collectors.toList());

		return adminMaterialMasterRepository.saveAll(savedMaterials);
	}

	@Override
	public Page<AdminMaterialMasterResponseWithImageDto> getAdminMaterialMaster(String materialCategory,
			String materialSubCategory, String brand, String modelNo, String size, String shape, String userId,
			Pageable pageable, Map<String, String> requestParams) throws AccessDeniedException {

		// Validate params
		List<String> expectedParams = Arrays.asList("materialCategory", "materialSubCategory", "brand", "modelNo",
				"brandsize", "shape", "userId");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminMaterialMaster> query = cb.createQuery(AdminMaterialMaster.class);
		Root<AdminMaterialMaster> root = query.from(AdminMaterialMaster.class);

		List<Predicate> predicates = new ArrayList<>();
		if (materialCategory != null && !materialCategory.trim().isEmpty())
			predicates.add(cb.equal(root.get("materialCategory"), materialCategory));
		if (materialSubCategory != null && !materialSubCategory.trim().isEmpty())
			predicates.add(cb.equal(root.get("materialSubCategory"), materialSubCategory));
		if (brand != null && !brand.trim().isEmpty())
			predicates.add(cb.equal(root.get("brand"), brand));
		if (modelNo != null && !modelNo.trim().isEmpty())
			predicates.add(cb.equal(root.get("modelNo"), modelNo));
		if (size != null && !size.trim().isEmpty())
			predicates.add(cb.equal(root.get("size"), size));
		if (shape != null && !shape.trim().isEmpty())
			predicates.add(cb.equal(root.get("shape"), shape));
		if (userId != null && !userId.trim().isEmpty())
			predicates.add(cb.equal(root.get("updatedBy"), userId));

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<AdminMaterialMaster> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		List<AdminMaterialMaster> materials = typedQuery.getResultList();

		// ✅ Fetch all image records for the same SKUs
		List<String> skuIds = materials.stream().map(AdminMaterialMaster::getSkuId).toList();
		if (skuIds.isEmpty()) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		}

		CriteriaQuery<UploadMatericalMasterImages> imgQuery = cb.createQuery(UploadMatericalMasterImages.class);
		Root<UploadMatericalMasterImages> imgRoot = imgQuery.from(UploadMatericalMasterImages.class);
		imgQuery.select(imgRoot).where(imgRoot.get("skuId").in(skuIds));
		List<UploadMatericalMasterImages> images = entityManager.createQuery(imgQuery).getResultList();

		// ✅ Map images by skuId
		Map<String, UploadMatericalMasterImages> imageMap = images.stream()
				.collect(Collectors.toMap(UploadMatericalMasterImages::getSkuId, img -> img));

		// ✅ Merge material + images
		List<AdminMaterialMasterResponseWithImageDto> mergedList = materials.stream().map(mat -> {
			AdminMaterialMasterResponseWithImageDto dto = new AdminMaterialMasterResponseWithImageDto();
			BeanUtils.copyProperties(mat, dto);
			UploadMatericalMasterImages img = imageMap.get(mat.getSkuId());
			if (img != null) {
				dto.setMaterialMasterImage1(img.getMaterialMasterImage1());
				dto.setMaterialMasterImage2(img.getMaterialMasterImage2());
				dto.setMaterialMasterImage3(img.getMaterialMasterImage3());
				dto.setMaterialMasterImage4(img.getMaterialMasterImage4());
				dto.setMaterialMasterImage5(img.getMaterialMasterImage5());
			}
			return dto;
		}).toList();

		// ✅ Count query (REBUILD predicates for countRoot)
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AdminMaterialMaster> countRoot = countQuery.from(AdminMaterialMaster.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (materialCategory != null && !materialCategory.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("materialCategory"), materialCategory));
		if (materialSubCategory != null && !materialSubCategory.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("materialSubCategory"), materialSubCategory));
		if (brand != null && !brand.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		if (modelNo != null && !modelNo.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("modelNo"), modelNo));
		if (size != null && !size.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("size"), size));
		if (shape != null && !shape.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("shape"), shape));
		if (userId != null && !userId.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("updatedBy"), userId));

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(mergedList, pageable, total);
	}

	@Transactional
	@Override
	public ResponseEntity<ResponseModel> uploadDoc(RegSource regSource, String skuId,
			MultipartFile materialMasterImage1, MultipartFile materialMasterImage2, MultipartFile materialMasterImage3,
			MultipartFile materialMasterImage4, MultipartFile materialMasterImage5) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		ResponseModel response = new ResponseModel();

		// 1. Check if skuId exists in AdminMaterialMaster
		Optional<AdminMaterialMaster> adminMaterial = adminMaterialMasterRepository.findBySkuId(skuId);
		if (adminMaterial.isEmpty()) {
			response.setError("true");
			response.setMsg("SKU ID not found in AdminMaterialMaster.");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		// 2. Directory for S3
		String directoryPath = "adminMaterialMaster/" + skuId + "/";

		// 3. Prepare new UploadMatericalMasterImages entity
		UploadMatericalMasterImages uploadEntity = new UploadMatericalMasterImages();
		uploadEntity.setSkuId(skuId);
		uploadEntity.setUpdatedBy(userInfo.userId);
		uploadEntity.setUpdatedDate(new Date());

		if (materialMasterImage1 != null && !materialMasterImage1.isEmpty()) {
			String path1 = directoryPath + materialMasterImage1.getOriginalFilename();
			String link1 = awsConfig.uploadFileToS3Bucket(path1, materialMasterImage1);
			uploadEntity.setMaterialMasterImage1(link1);
		}

		if (materialMasterImage2 != null && !materialMasterImage2.isEmpty()) {
			String path2 = directoryPath + materialMasterImage2.getOriginalFilename();
			String link2 = awsConfig.uploadFileToS3Bucket(path2, materialMasterImage2);
			uploadEntity.setMaterialMasterImage2(link2);
		}

		if (materialMasterImage3 != null && !materialMasterImage3.isEmpty()) {
			String path3 = directoryPath + materialMasterImage3.getOriginalFilename();
			String link3 = awsConfig.uploadFileToS3Bucket(path3, materialMasterImage3);
			uploadEntity.setMaterialMasterImage3(link3);
		}

		if (materialMasterImage4 != null && !materialMasterImage4.isEmpty()) {
			String path4 = directoryPath + materialMasterImage4.getOriginalFilename();
			String link4 = awsConfig.uploadFileToS3Bucket(path4, materialMasterImage4);
			uploadEntity.setMaterialMasterImage4(link4);
		}

		if (materialMasterImage5 != null && !materialMasterImage5.isEmpty()) {
			String path5 = directoryPath + materialMasterImage5.getOriginalFilename();
			String link5 = awsConfig.uploadFileToS3Bucket(path5, materialMasterImage5);
			uploadEntity.setMaterialMasterImage5(link5);
		}

		// 4. Save to UploadMatericalMasterImages table
		uploadMatericalMasterImagesRepository.save(uploadEntity);

		// 5. Response
		response.setError("false");
		response.setMsg("Admin Material images uploaded and stored successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public List<String> findDistinctBrandByMaterialCategory(String materialCategory, String materialSubCategory,
			Map<String, String> requestParams) {
		List<String> expectedParams = Arrays.asList("materialCategory", "materialSubCategory");
		for (String paramName : requestParams.keySet()) {
			if (!expectedParams.contains(paramName)) {
				throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
			}
		}
		return adminMaterialMasterRepository.findDistinctBrandByMaterialCategory(materialCategory, materialSubCategory);
	}

	@Override
//	public List<String> findDistinctMaterialCategory() {
//		return adminMaterialMasterRepository.findDistinctMaterialCategory();
//	}
	public List<Map<String, Object>> findDistinctMaterialCategoryWithSubCategory() {
		List<Object[]> results = adminMaterialMasterRepository.findCategoryAndSubCategory();
		Map<String, Set<String>> grouped = new LinkedHashMap<>();

		for (Object[] row : results) {
			String category = (String) row[0];
			String subCategory = (String) row[1];
			grouped.computeIfAbsent(category, k -> new LinkedHashSet<>()).add(subCategory);
		}

		// Convert map to list of JSON-friendly objects
		List<Map<String, Object>> response = new ArrayList<>();
		for (Map.Entry<String, Set<String>> entry : grouped.entrySet()) {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("category", entry.getKey());
			map.put("subCategories", new ArrayList<>(entry.getValue()));
			response.add(map);
		}
		return response;
	}

	@Override
	public AdminMaterialMasterResponseDTO getMaterialsWithUserInfo(String materialCategory, String materialSubCategory,
	        String brand, String location) {

	    // Step 1: Fetch all materials (based on category/brand/subCategory)
	    List<AdminMaterialMaster> materials = adminMaterialMasterRepository.searchMaterials(materialCategory,
	            materialSubCategory, brand);

	    if (materials.isEmpty()) {
	        return new AdminMaterialMasterResponseDTO(Collections.emptyList(), Collections.emptyList(),
	                Collections.emptyList());
	    }

	    List<AdminDetailsDto> adminDtos = new ArrayList<>();
	    List<MaterialSupplierDto> supplierDtos = new ArrayList<>();
	    Set<String> seenAdminIds = new HashSet<>();
	    Set<String> seenSupplierIds = new HashSet<>();

	    // Step 2: Fetch all images for materials
	    List<String> skuIds = materials.stream().map(AdminMaterialMaster::getSkuId).toList();
	    List<UploadMatericalMasterImages> images = uploadMatericalMasterImagesRepository.findAllById(skuIds);

	    // Map images by skuId
	    Map<String, UploadMatericalMasterImages> imageMap = images.stream()
	            .collect(Collectors.toMap(UploadMatericalMasterImages::getSkuId, img -> img));

	    // Step 3: Build final material DTO list with proper location filtering
	    List<AdminMaterialMasterResponseWithImageDto> materialDtos = new ArrayList<>();

	    for (AdminMaterialMaster material : materials) {
	        String userId = material.getUpdatedBy();

	        // Fetch supplier for this material
	        MaterialSupplierQuotationUser supplier = materialSupplierQuotationUserDAO.findByBodSeqNo(userId);

	        // Apply location filter if provided
	        if (location != null && !location.isEmpty()) {
	            if (supplier == null || !location.equalsIgnoreCase(supplier.getLocation())) {
	                continue; // skip this material if location doesn't match
	            }
	        }

	        // --- Map material + images ---
	        AdminMaterialMasterResponseWithImageDto dto = new AdminMaterialMasterResponseWithImageDto();
	        BeanUtils.copyProperties(material, dto);

	        UploadMatericalMasterImages img = imageMap.get(material.getSkuId());
	        if (img != null) {
	            dto.setMaterialMasterImage1(img.getMaterialMasterImage1());
	            dto.setMaterialMasterImage2(img.getMaterialMasterImage2());
	            dto.setMaterialMasterImage3(img.getMaterialMasterImage3());
	            dto.setMaterialMasterImage4(img.getMaterialMasterImage4());
	            dto.setMaterialMasterImage5(img.getMaterialMasterImage5());
	        }

	        materialDtos.add(dto);

	        // --- Admin details ---
	        AdminDetails user = adminRepo.findByAdminId(userId);
	        if (user != null && seenAdminIds.add(user.getAdminId())) {
	            adminDtos.add(toAdminDto(user));
	        }

	        // --- Supplier details ---
	        if (supplier != null && seenSupplierIds.add(supplier.getBodSeqNo())) {
	            supplierDtos.add(toSupplierDto(supplier));
	        }
	    }

	    return new AdminMaterialMasterResponseDTO(materialDtos, adminDtos, supplierDtos);
	}


	// --- Mapping helpers ---
	private AdminDetailsDto toAdminDto(AdminDetails admin) {
		AdminDetailsDto dto = new AdminDetailsDto();
		dto.setId(admin.getId());
		dto.setMobile(admin.getMobile());
		dto.setEmail(admin.getEmail());
		dto.setRegDate(admin.getRegDate());
		dto.setStatus(admin.getStatus());
		dto.setAdminId(admin.getAdminId());
		dto.setAdminName(admin.getAdminName());
		return dto;
	}

	private MaterialSupplierDto toSupplierDto(MaterialSupplierQuotationUser supplier) {
		MaterialSupplierDto dto = new MaterialSupplierDto();
		dto.setBodSeqNo(supplier.getBodSeqNo());
		dto.setName(supplier.getName());
		dto.setBusinessName(supplier.getBusinessName());
		dto.setMobile(supplier.getMobile());
		dto.setEmail(supplier.getEmail());
		dto.setAddress(supplier.getAddress());
		dto.setCity(supplier.getCity());
		dto.setDistrict(supplier.getDistrict());
		dto.setState(supplier.getState());
		dto.setLocation(supplier.getLocation());
		return dto;
	}

}
