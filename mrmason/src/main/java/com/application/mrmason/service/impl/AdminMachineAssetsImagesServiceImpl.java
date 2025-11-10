package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminMachineAssetsImages;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminMachineAssetsImagesRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.AdminMachineAssetsImagesService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class AdminMachineAssetsImagesServiceImpl implements AdminMachineAssetsImagesService {
	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AdminMachineAssetsImagesRepo adminMachineAssetsImagesRepo;

	@Autowired
	private AWSConfig awsConfig;

	@Override
	@Transactional
	public AdminMachineAssetsImages addMachineAssetsImage(AdminMachineAssetsImages adminMachineAssetsImage) {
		UserInfo userInfo = getLoggedInUserInfo();
		String pk = adminMachineAssetsImage.getCategory() + "_" + adminMachineAssetsImage.getMachineId() + "_"
				+ adminMachineAssetsImage.getBrand() + "_" + adminMachineAssetsImage.getModelName();

		// Try to find existing entity by PK
		AdminMachineAssetsImages existing = entityManager.find(AdminMachineAssetsImages.class, pk);
		Date now = new Date();
		if (existing != null) {
			existing.setImageUrl(adminMachineAssetsImage.getImageUrl());
			existing.setSubCategory(adminMachineAssetsImage.getSubCategory());
			existing.setUpdatedBy(userInfo.userId);
			existing.setUpdatedDate(now);
			existing.setCategory(adminMachineAssetsImage.getCategory());
			existing.setMachineId(adminMachineAssetsImage.getMachineId());
			existing.setBrand(adminMachineAssetsImage.getBrand());
			existing.setModelId(adminMachineAssetsImage.getModelId());
			existing.setModelName(adminMachineAssetsImage.getModelName());

			AdminMachineAssetsImages merged = entityManager.merge(existing);
			return merged;
		} else {
			adminMachineAssetsImage.setCategoryMachineBrandModel(pk);
			adminMachineAssetsImage.setUpdatedBy(userInfo.userId);
			adminMachineAssetsImage.setUpdatedDate(now);

			entityManager.persist(adminMachineAssetsImage);
			return adminMachineAssetsImage;
		}
	}

	private static class UserInfo {

		String userId;

		UserInfo(String userId) {
			this.userId = userId;
		}
	}

	private UserInfo getLoggedInUserInfo() {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());
		// ✅ Ensure at least one role exists
		if (roleNames.isEmpty()) {
			throw new ResourceNotFoundException("No roles found for user: " + loggedInUserEmail);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));
		String userId;

		if (userType == UserType.Adm) {
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getAdminId(); // or admin.getAdminId() if preferred
		} else {
			throw new ResourceNotFoundException("Access restricted for non-admin roles: " + roleNames);
		}

		return new UserInfo(userId);
	}

	@Override
	@Transactional
	public AdminMachineAssetsImages updateMachineAssetsImage(AdminMachineAssetsImages adminMachineAssetsImage) {

		// ✅ Get logged-in user details
		UserInfo userInfo = getLoggedInUserInfo();

		// ✅ Use provided primary key directly
		String pk = adminMachineAssetsImage.getCategoryMachineBrandModel();

		if (pk == null || pk.isEmpty()) {
			throw new IllegalArgumentException("categoryMachineBrandModel is required for update");
		}

		// ✅ Fetch the existing record using PK
		AdminMachineAssetsImages existing = entityManager.find(AdminMachineAssetsImages.class, pk);

		if (existing == null) {
			throw new ResourceNotFoundException("Machine asset not found for ID: " + pk);
		}

		// ✅ Only update allowed fields
		if (adminMachineAssetsImage.getModelId() != null) {
			existing.setModelId(adminMachineAssetsImage.getModelId());
		}

		if (adminMachineAssetsImage.getSubCategory() != null) {
			existing.setSubCategory(adminMachineAssetsImage.getSubCategory());
		}

		existing.setUpdatedBy(userInfo.userId);
		existing.setUpdatedDate(new Date());

		// ✅ Save updated entity
		return entityManager.merge(existing);
	}

	@Override
	public ResponseEntity<ResponseModel> uploadProfileImage(String categoryMachineBrandModel, MultipartFile imageUrl)
			throws AccessDeniedException {
		ResponseModel response = new ResponseModel();

		// ✅ 1. Validate and find existing record
		Optional<AdminMachineAssetsImages> existingOpt = adminMachineAssetsImagesRepo
				.findById(categoryMachineBrandModel);
		if (existingOpt.isEmpty()) {
			response.setError("true");
			response.setMsg("Machine asset ID not found in AdminMachineAssetsImages.");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		AdminMachineAssetsImages existing = existingOpt.get();

		// ✅ 2. Validate photo
		if (imageUrl == null || imageUrl.isEmpty()) {
			response.setError("true");
			response.setMsg("No photo provided for upload.");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			// ✅ 3. Get logged-in user info
			UserInfo userInfo = getLoggedInUserInfo();

			// ✅ 4. Build S3 directory and file path
			String directoryPath = "uploadphoto/" + categoryMachineBrandModel + "/";
			String filePath = directoryPath + imageUrl.getOriginalFilename();

			// ✅ 5. Upload to AWS S3
			String uploadedUrl = awsConfig.uploadFileToS3Bucket(filePath, imageUrl);

			// ✅ 6. Update entity fields
			existing.setImageUrl(uploadedUrl);
			existing.setUpdatedBy(userInfo.userId);
			existing.setUpdatedDate(new Date());

			// ✅ 7. Save
			adminMachineAssetsImagesRepo.save(existing);

			// ✅ 8. Response success
			response.setError("false");
			response.setMsg("Profile photo uploaded successfully.");
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setError("true");
			response.setMsg("Error while uploading image: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Page<AdminMachineAssetsImages> get(String categoryMachineBrandModel, String category, String machineId,
			String brand, String modelId, String modelName, String subCategory, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// --- MAIN QUERY ---
		CriteriaQuery<AdminMachineAssetsImages> query = cb.createQuery(AdminMachineAssetsImages.class);
		Root<AdminMachineAssetsImages> root = query.from(AdminMachineAssetsImages.class);

		List<Predicate> predicates = new ArrayList<>();

		if (categoryMachineBrandModel != null && !categoryMachineBrandModel.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("categoryMachineBrandModel"), categoryMachineBrandModel));
		}
		if (category != null && !category.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("category"), category));
		}
		if (machineId != null && !machineId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("machineId"), machineId));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (modelId != null && !modelId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelId"), modelId));
		}
		if (modelName != null && !modelName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelName"), modelName));
		}
		if (subCategory != null && !subCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("subCategory"), subCategory));
		}
		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

		// Pagination
		TypedQuery<AdminMachineAssetsImages> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// --- COUNT QUERY ---
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AdminMachineAssetsImages> countRoot = countQuery.from(AdminMachineAssetsImages.class);
		countQuery.select(cb.count(countRoot));
		List<Predicate> countPredicates = new ArrayList<>();

		if (categoryMachineBrandModel != null && !categoryMachineBrandModel.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("categoryMachineBrandModel"), categoryMachineBrandModel));
		}
		if (category != null && !category.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("category"), category));
		}
		if (machineId != null && !machineId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("machineId"), machineId));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		}
		if (modelId != null && !modelId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("modelId"), modelId));
		}
		if (modelName != null && !modelName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("modelName"), modelName));
		}
		if (subCategory != null && !subCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("subCategory"), subCategory));
		}
		countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

}
