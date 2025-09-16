package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.entity.UploadMatericalMasterImages;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminMaterialMasterRepository;
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
public class AdminMaterialMasterServiceImpl implements AdminMaterialMasterService{
	
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

	@Override
    public List<AdminMaterialMaster> createAdminMaterialMaster(List<AdminMaterialMaster> requestDTO) throws AccessDeniedException {
        AdminInfo userInfo=getLoggedInAdminInfo();
	    if (!UserType.Adm.name().equals(userInfo.role)) {
	        throw new AccessDeniedException("Only Admin users can access this API.");
	    }
        List<AdminMaterialMaster> updatedList = requestDTO.stream().map(material -> {
            int randomSixDigit = new Random().nextInt(900000) + 100000;
            String skuId = userInfo.adminId + "_" + material.getBrand() + "_" + material.getModelNo() + "_" +
                           material.getMaterialCategory() + "_" + material.getMaterialSubCategory() + "_" + randomSixDigit;

            material.setSkuId(skuId);
            material.setUpdatedBy(userInfo.adminId);
            material.setUpdatedDate(new Date());
            material.setStatus("Active");
            return material;
        }).collect(Collectors.toList());

        return adminMaterialMasterRepository.saveAll(updatedList);
    }

	private static class AdminInfo {

		public String role;
		String adminId;
		AdminInfo(String adminId,String role) {
			this.adminId=adminId;
			this.role = role;
		}
	}

	private AdminInfo getLoggedInAdminInfo( ) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());
		String userId = null;
		String role = roleNames.get(0);
		UserType userType = UserType.valueOf(role);
		if (userType == UserType.Adm) {
			AdminDetails user = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getAdminId();
		} 
		return new AdminInfo(userId, role);
	}


	@Override
	public List<AdminMaterialMaster> updateAdminMaterialMasters(List<AdminMaterialMaster> updatedList) throws AccessDeniedException {
		 AdminInfo userInfo = getLoggedInAdminInfo();
		    if (!UserType.Adm.name().equals(userInfo.role)) {
		        throw new AccessDeniedException("Only Admin users can access this API.");
		    }

	    List<AdminMaterialMaster> savedMaterials = updatedList.stream().map(material -> {
	        Optional<AdminMaterialMaster> existingOpt = adminMaterialMasterRepository.findBySkuId(material.getSkuId());

	        if (existingOpt.isPresent()) {
	            AdminMaterialMaster existing = existingOpt.get();

	            // Do NOT allow updating these fields:
	            material.setBrand(existing.getBrand());
	            material.setModelNo(existing.getModelNo());
	            material.setMaterialCategory(existing.getMaterialCategory());
	            material.setMaterialSubCategory(existing.getMaterialSubCategory());

	            // Allow updating these fields:
	            material.setUpdatedBy(userInfo.adminId);
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
	public Page<AdminMaterialMaster> getAdminMaterialMaster(String materialCategory,
			String materialSubCategory, String brand, String modelNo, String size,String shape, Pageable pageable)
			throws AccessDeniedException {

		 AdminInfo userInfo = getLoggedInAdminInfo();
		    if (!UserType.Adm.name().equals(userInfo.role)) {
		        throw new AccessDeniedException("Only Admin users can access this API.");
		    }
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminMaterialMaster> query = cb.createQuery(AdminMaterialMaster.class);
		Root<AdminMaterialMaster> root = query.from(AdminMaterialMaster.class);
		List<Predicate> predicates = new ArrayList<>();

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
		if (size != null && !size.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("size"), size));
		}
		if (shape != null && !shape.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("shape"), shape));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<AdminMaterialMaster> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AdminMaterialMaster> countRoot = countQuery.from(AdminMaterialMaster.class);
		List<Predicate> countPredicates = new ArrayList<>();

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
			countPredicates.add(cb.equal(countRoot.get("modelNo"), modelNo));
		}
		if (size != null && !size.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("size"), size));
		}
		if (shape != null && !shape.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("shape"), shape));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}


		@Transactional
		@Override
		public ResponseEntity<ResponseModel> uploadDoc(String skuId, MultipartFile materialMasterImage1,
		                                               MultipartFile materialMasterImage2,
		                                               MultipartFile materialMasterImage3,
		                                               MultipartFile materialMasterImage4,
		                                               MultipartFile materialMasterImage5) throws AccessDeniedException {

		    AdminInfo userInfo = getLoggedInAdminInfo();
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
		    uploadEntity.setUpdatedBy(userInfo.adminId);
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
		public List<String> findDistinctBrandByMaterialCategory(String materialCategory, Map<String, String> requestParams) {
			List<String> expectedParams = Arrays.asList("materialCategory");
			for (String paramName : requestParams.keySet()) {
				if (!expectedParams.contains(paramName)) {
					throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
				}
			}
			return adminMaterialMasterRepository.findDistinctBrandByMaterialCategory(materialCategory);
		}

		@Override
		public List<String> findDistinctMaterialCategory() {
			return adminMaterialMasterRepository.findDistinctMaterialCategory();
		}

}
