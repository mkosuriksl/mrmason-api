package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.MaterialSupplierAssets;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerAssetsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.MaterialSupplierAssetsRepo;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.CustomerAssetsService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class CustomerAssetsServiceImpl implements CustomerAssetsService {
	@Autowired
	CustomerAssetsRepo assetRepo;
	@Autowired
	MaterialSupplierAssetsRepo materialSupplierAssetsRepo;
	@Autowired
	CustomerRegistrationRepo regiRepo;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	public AdminDetailsRepo adminRepo;
	@Autowired
	UserDAO userDAO;

	@Override
	public CustomerAssets saveAssets(CustomerAssets asset) {
		if (regiRepo.findByUserid(asset.getUserId()) != null) {
			return assetRepo.save(asset);
		}
		return null;

	}

	@Override
	public CustomerAssetDto updateAssets(CustomerAssetDto asset, RegSource regSource) {

		String prefix = asset.getUserId().substring(0, 2).toUpperCase();

		CustomerAssetDto responseDto = new CustomerAssetDto();
		if (prefix.equals("MS") || prefix.equals("SP")) {
			Optional<MaterialSupplierAssets> msAssetOpt = materialSupplierAssetsRepo
					.findByUserIdAndAssetId(asset.getUserId(), asset.getAssetId());

			if (msAssetOpt.isPresent()) {
				MaterialSupplierAssets msAsset = msAssetOpt.get();

				// Update fields
				msAsset.setAssetCat(asset.getAssetCat());
				msAsset.setAssetSubCat(asset.getAssetSubCat());
				msAsset.setDistrict(asset.getDistrict());
				msAsset.setDoorNo(asset.getDoorNo());
				msAsset.setLocation(asset.getLocation());
				msAsset.setPinCode(asset.getPinCode());
				msAsset.setState(asset.getState());
				msAsset.setStreet(asset.getStreet());
				msAsset.setTown(asset.getTown());
				msAsset.setAssetBrand(asset.getAssetBrand());
				msAsset.setAssetModel(asset.getAssetModel());

				MaterialSupplierAssets updated = materialSupplierAssetsRepo.save(msAsset);

				// Map to DTO for response
				responseDto.setAssetCat(updated.getAssetCat());
				responseDto.setAssetSubCat(updated.getAssetSubCat());
				responseDto.setDistrict(updated.getDistrict());
				responseDto.setDoorNo(updated.getDoorNo());
				responseDto.setLocation(updated.getLocation());
				responseDto.setPinCode(updated.getPinCode());
				responseDto.setState(updated.getState());
				responseDto.setStreet(updated.getStreet());
				responseDto.setTown(updated.getTown());
				responseDto.setAssetBrand(updated.getAssetBrand());
				responseDto.setAssetModel(updated.getAssetModel());
				responseDto.setAssetId(updated.getAssetId());
				responseDto.setUserId(updated.getUserId());
				responseDto.setPlanId(updated.getPlanId());
				responseDto.setMembershipExp(updated.getMembershipExpDb());
				responseDto.setRegDate(updated.getRegDateFormatted());
			} else {
				throw new ResourceNotFoundException("Asset not found for user: " + asset.getUserId());
			}
		}

		// =========================
		// CASE 2: EC
		// =========================
		else if (prefix.equals("CU")) {
			Optional<CustomerAssets> assetDb = assetRepo.findByUserIdAndAssetId(asset.getUserId(), asset.getAssetId());

			if (assetDb.isPresent()) {
				CustomerAssets userAsset = assetDb.get();

				userAsset.setAssetCat(asset.getAssetCat());
				userAsset.setAssetSubCat(asset.getAssetSubCat());
				userAsset.setDistrict(asset.getDistrict());
				userAsset.setDoorNo(asset.getDoorNo());
				userAsset.setLocation(asset.getLocation());
				userAsset.setPinCode(asset.getPinCode());
				userAsset.setState(asset.getState());
				userAsset.setStreet(asset.getStreet());
				userAsset.setTown(asset.getTown());
				userAsset.setAssetBrand(asset.getAssetBrand());
				userAsset.setAssetModel(asset.getAssetModel());

				CustomerAssets updated = assetRepo.save(userAsset);

				// Map to DTO
				responseDto.setAssetCat(updated.getAssetCat());
				responseDto.setAssetSubCat(updated.getAssetSubCat());
				responseDto.setDistrict(updated.getDistrict());
				responseDto.setDoorNo(updated.getDoorNo());
				responseDto.setLocation(updated.getLocation());
				responseDto.setPinCode(updated.getPinCode());
				responseDto.setState(updated.getState());
				responseDto.setStreet(updated.getStreet());
				responseDto.setTown(updated.getTown());
				responseDto.setAssetBrand(updated.getAssetBrand());
				responseDto.setAssetModel(updated.getAssetModel());
				responseDto.setAssetId(updated.getAssetId());
				responseDto.setUserId(updated.getUserId());
				responseDto.setPlanId(updated.getPlanId());
				responseDto.setMembershipExp(updated.getMembershipExpDb());
				responseDto.setRegDate(updated.getRegDateFormatted());
			} else {
				throw new ResourceNotFoundException("Asset not found for user: " + asset.getUserId());
			}
		} else {
			throw new ResourceNotFoundException("UserId not found: " + asset.getUserId());
		}

		return responseDto;
	}

	@Override
	public Page<?> getAssets(String userId, String assetId, String location, String assetCat, String assetSubCat,
			String assetModel, String assetBrand, Pageable pageable, RegSource regSource) {
		String prefix = userId.substring(0, 2).toUpperCase();

		CustomerAssetDto responseDto = new CustomerAssetDto();
		if (prefix.equals("MS") || prefix.equals("SP")) {
			return getMaterialSupplierAssets(userId, assetId, location, assetCat, assetSubCat, assetModel, assetBrand,
					pageable);
		} else if (prefix.equals("CU")) {
			return getCustomerAssetsInternal(userId, assetId, location, assetCat, assetSubCat, assetModel, assetBrand,
					pageable);
		} else {
			throw new ResourceNotFoundException("userId not found: " + userId);
		}
	}

	private Page<CustomerAssets> getCustomerAssetsInternal(String userId, String assetId, String location,
			String assetCat, String assetSubCat, String assetModel, String assetBrand, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CustomerAssets> query = cb.createQuery(CustomerAssets.class);
		Root<CustomerAssets> root = query.from(CustomerAssets.class);

		List<Predicate> predicates = new ArrayList<>();

		if (userId != null && !userId.trim().isEmpty())
			predicates.add(cb.equal(root.get("userId"), userId));
		if (assetId != null && !assetId.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetId"), assetId));
		if (location != null && !location.trim().isEmpty())
			predicates.add(cb.equal(root.get("location"), location));
		if (assetCat != null && !assetCat.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetCat"), assetCat));
		if (assetSubCat != null && !assetSubCat.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetSubCat"), assetSubCat));
		if (assetModel != null && !assetModel.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetModel"), assetModel));
		if (assetBrand != null && !assetBrand.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetBrand"), assetBrand));

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

		TypedQuery<CustomerAssets> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// ✅ Build count query properly
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<CustomerAssets> countRoot = countQuery.from(CustomerAssets.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (userId != null && !userId.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		if (assetId != null && !assetId.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetId"), assetId));
		if (location != null && !location.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("location"), location));
		if (assetCat != null && !assetCat.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetCat"), assetCat));
		if (assetSubCat != null && !assetSubCat.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetSubCat"), assetSubCat));
		if (assetModel != null && !assetModel.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetModel"), assetModel));
		if (assetBrand != null && !assetBrand.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetBrand"), assetBrand));

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	private Page<MaterialSupplierAssets> getMaterialSupplierAssets(String userId, String assetId, String location,
			String assetCat, String assetSubCat, String assetModel, String assetBrand, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MaterialSupplierAssets> query = cb.createQuery(MaterialSupplierAssets.class);
		Root<MaterialSupplierAssets> root = query.from(MaterialSupplierAssets.class);

		List<Predicate> predicates = new ArrayList<>();
		if (userId != null && !userId.trim().isEmpty())
			predicates.add(cb.equal(root.get("userId"), userId));
		if (assetId != null && !assetId.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetId"), assetId));
		if (location != null && !location.trim().isEmpty())
			predicates.add(cb.equal(root.get("location"), location));
		if (assetCat != null && !assetCat.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetCat"), assetCat));
		if (assetSubCat != null && !assetSubCat.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetSubCat"), assetSubCat));
		if (assetModel != null && !assetModel.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetModel"), assetModel));
		if (assetBrand != null && !assetBrand.trim().isEmpty())
			predicates.add(cb.equal(root.get("assetBrand"), assetBrand));

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

		TypedQuery<MaterialSupplierAssets> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialSupplierAssets> countRoot = countQuery.from(MaterialSupplierAssets.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (userId != null && !userId.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		if (assetId != null && !assetId.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetId"), assetId));
		if (location != null && !location.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("location"), location));
		if (assetCat != null && !assetCat.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetCat"), assetCat));
		if (assetSubCat != null && !assetSubCat.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetSubCat"), assetSubCat));
		if (assetModel != null && !assetModel.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetModel"), assetModel));
		if (assetBrand != null && !assetBrand.trim().isEmpty())
			countPredicates.add(cb.equal(countRoot.get("assetBrand"), assetBrand));

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	public CustomerAssetDto getAssetByAssetId(CustomerAssets asset, RegSource regSource) {
		String prefix = asset.getUserId().substring(0, 2).toUpperCase();

		if (prefix.equals("MS") || prefix.equals("SP")) {
			MaterialSupplierAssets msAsset = new MaterialSupplierAssets();
			msAsset.setUserId(asset.getUserId());
			msAsset.setAssetCat(asset.getAssetCat());
			msAsset.setAssetSubCat(asset.getAssetSubCat());
			msAsset.setLocation(asset.getLocation());
			msAsset.setStreet(asset.getStreet());
			msAsset.setDoorNo(asset.getDoorNo());
			msAsset.setTown(asset.getTown());
			msAsset.setDistrict(asset.getDistrict());
			msAsset.setState(asset.getState());
			msAsset.setPinCode(asset.getPinCode());
			msAsset.setPlanId(asset.getPlanId());
			msAsset.setAssetBrand(asset.getAssetBrand());
			msAsset.setAssetModel(asset.getAssetModel());
			msAsset.setPlanId(asset.getPlanId());
			MaterialSupplierAssets savedAsset = materialSupplierAssetsRepo.save(msAsset);
			// Set generated ID back into DTO
			asset.setAssetId(savedAsset.getAssetId());
			asset.setMembershipExp(savedAsset.getMembershipExp());
		} else if (prefix.equals("CU")) {
			assetRepo.save(asset);
		} else {
			throw new ResourceNotFoundException("Not found userId " + asset.getUserId());
		}

		// Prepare DTO for response
		CustomerAssetDto assetDto = new CustomerAssetDto();
		assetDto.setAssetCat(asset.getAssetCat());
		assetDto.setAssetSubCat(asset.getAssetSubCat());
		assetDto.setDistrict(asset.getDistrict());
		assetDto.setDoorNo(asset.getDoorNo());
		assetDto.setLocation(asset.getLocation());
		assetDto.setPinCode(asset.getPinCode());
		assetDto.setState(asset.getState());
		assetDto.setStreet(asset.getStreet());
		assetDto.setTown(asset.getTown());
		assetDto.setAssetModel(asset.getAssetModel());
		assetDto.setRegDate(asset.getRegDateFormatted());
		assetDto.setPlanId(asset.getPlanId());
		assetDto.setMembershipExp(asset.getMembershipExpDb());
		assetDto.setAssetId(asset.getAssetId());
		assetDto.setUserId(asset.getUserId());
		assetDto.setAssetBrand(asset.getAssetBrand());

		return assetDto;
	}
}
