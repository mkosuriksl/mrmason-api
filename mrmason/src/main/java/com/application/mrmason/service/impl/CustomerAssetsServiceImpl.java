package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;

import com.application.mrmason.dto.CustomerAssetDto;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.repository.CustomerAssetsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
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
	CustomerRegistrationRepo regiRepo;
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public CustomerAssets saveAssets(CustomerAssets asset) {
		if (regiRepo.findByUserid(asset.getUserId()) != null) {
			return assetRepo.save(asset);
		}
		return null;

	}

	@Override
	public CustomerAssets updateAssets(CustomerAssetDto asset) {
		Optional<CustomerAssets> assetDb = assetRepo.findByUserIdAndAssetId(asset.getUserId(), asset.getAssetId());
		if (assetDb.isPresent()) {
			CustomerAssets user = assetDb.get();
			user.setAssetCat(asset.getAssetCat());
			user.setAssetSubCat(asset.getAssetSubCat());
			user.setDistrict(asset.getDistrict());
			user.setDoorNo(asset.getDoorNo());
			user.setLocation(asset.getLocation());
			user.setPinCode(asset.getPinCode());
			user.setState(asset.getState());
			user.setStreet(asset.getStreet());
			user.setTown(asset.getTown());
			user.setAssetBrand(asset.getAssetBrand());
			user.setAssetModel(asset.getAssetModel());
			return assetRepo.save(user);

		}
		return null;
	}

	@Override
	public Page<CustomerAssets> getCustomerAssets(String userId, String assetId, String location, String assetCat,
			String assetSubCat, String assetModel, String assetBrand, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CustomerAssets> query = cb.createQuery(CustomerAssets.class);
		Root<CustomerAssets> root = query.from(CustomerAssets.class);

		List<Predicate> predicates = new ArrayList<>();

		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (assetId != null && !assetId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("assetId"), assetId));
		}
		if (location != null && !location.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("location"), location));
		}
		if (assetCat != null && !assetCat.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("assetCat"), assetCat));
		}
		if (assetSubCat != null && !assetSubCat.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("assetSubCat"), assetSubCat));
		}
		if (assetModel != null && !assetModel.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("assetModel"), assetModel));
		}
		if (assetBrand != null && !assetBrand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("assetBrand"), assetBrand));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<CustomerAssets> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<CustomerAssets> countRoot = countQuery.from(CustomerAssets.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		}
		if (assetId != null && !assetId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("assetId"), assetId));
		}
		if (location != null && !location.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("location"), location));
		}
		if (assetCat != null && !assetCat.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("assetCat"), assetCat));
		}
		if (assetSubCat != null && !assetSubCat.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("assetSubCat"), assetSubCat));
		}
		if (assetModel != null && !assetModel.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("assetModel"), assetModel));
		}
		if (assetBrand != null && !assetBrand.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("assetBrand"), assetBrand));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}
//	public List<CustomerAssets> getAssets(String userId,String assetId,String location,String assetCat,String assetSubCat,String assetModel,String assetBrand) {
//
//		if(userId!=null && assetId==null && location==null && assetCat==null && assetSubCat==null && assetModel==null && assetBrand==null) {
//			Optional<List<CustomerAssets>> user=Optional.of((assetRepo.findByUserIdOrderByIdDesc(userId)));
//			return user.get();
//		}else if(userId==null && assetId!=null && location==null && assetCat==null && assetSubCat==null && assetModel==null && assetBrand==null) {
//			Optional<List<CustomerAssets>> user=Optional.of((assetRepo.findByAssetIdOrderByIdDesc(assetId)));
//			return user.get();
//		}else if(userId==null && assetId==null && location!=null && assetCat==null && assetSubCat==null && assetModel==null && assetBrand==null) {
//			Optional<List<CustomerAssets>> user=Optional.of((assetRepo.findByLocationOrderByIdDesc(location)));
//			return user.get();
//		}else if(userId==null && assetId==null && location==null && assetCat!=null && assetSubCat==null && assetModel==null && assetBrand==null) {
//			Optional<List<CustomerAssets>> user=Optional.of((assetRepo.findByAssetCatOrderByIdDesc(assetCat)));
//			return user.get();
//		}else if(userId==null && assetId==null && location==null && assetCat==null && assetSubCat!=null && assetModel==null && assetBrand==null) {
//			Optional<List<CustomerAssets>> user=Optional.of((assetRepo.findByAssetSubCatOrderByIdDesc(assetSubCat)));
//			return user.get();
//		}else if(userId==null && assetId==null && location==null && assetCat==null && assetSubCat==null && assetModel!=null && assetBrand==null) {
//			Optional<List<CustomerAssets>> user=Optional.of((assetRepo.findByAssetModelOrderByIdDesc(assetModel)));
//			return user.get();
//		}else if(userId==null && assetId==null && location==null && assetCat==null && assetSubCat==null && assetModel==null && assetBrand!=null) {
//			Optional<List<CustomerAssets>> user=Optional.of((assetRepo.findByAssetBrandOrderByIdDesc(assetBrand)));
//			return user.get();
//		}
//		return null;

//	}

	@Override
	public CustomerAssetDto getAssetByAssetId(String assetId) {
		if (assetRepo.findAllByAssetId(assetId) != null) {
			Optional<CustomerAssets> assetDb = assetRepo.findAllByAssetId(assetId);
			CustomerAssets assetData = assetDb.get();
			CustomerAssetDto assetDto = new CustomerAssetDto();

			assetDto.setAssetCat(assetData.getAssetCat());
			assetDto.setAssetSubCat(assetData.getAssetSubCat());
			assetDto.setDistrict(assetData.getDistrict());
			assetDto.setDoorNo(assetData.getDoorNo());
			assetDto.setLocation(assetData.getLocation());
			assetDto.setPinCode(assetData.getPinCode());
			assetDto.setState(assetData.getState());
			assetDto.setStreet(assetData.getStreet());
			assetDto.setTown(assetData.getTown());
			assetDto.setAssetModel(assetData.getAssetModel());
			assetDto.setRegDate(assetData.getRegDateFormatted());
			assetDto.setPlanId(assetData.getPlanId());
			assetDto.setMembershipExp(assetData.getMembershipExpDb());
			assetDto.setAssetId(assetData.getAssetId());
			assetDto.setUserId(assetData.getUserId());
			assetDto.setAssetBrand(assetData.getAssetBrand());
			return assetDto;

		}
		return null;
	}

}
