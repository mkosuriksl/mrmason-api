package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.AdminAsset;
import com.application.mrmason.repository.AdminAssetRepo;
import com.application.mrmason.service.AdminAssetService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminAssetServiceImpl implements AdminAssetService {

	@Autowired
	public AdminAssetRepo adminAssetRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public AdminAsset addAdminAssets(AdminAsset asset) {

		return adminAssetRepo.save(asset);
	}

//	@Override
//	public List<AdminAsset> getAssets(String assetId, String assetCat, String assetSubCat, String assetModel, String assetBrand) {
//	    if (assetId != null) {
//	        return adminAssetRepo.findByAssetIdOrderByAddedDateDesc(assetId);
//	    } else if (assetCat != null) {
//	        return adminAssetRepo.findByAssetCatOrderByAddedDateDesc(assetCat);
//	    } else if (assetSubCat != null) {
//	        return adminAssetRepo.findByAssetSubCatOrderByAddedDateDesc(assetSubCat);
//	    } else if (assetModel != null) {
//	        return adminAssetRepo.findByAssetModelOrderByAddedDateDesc(assetModel);
//	    } else if (assetBrand != null) {
//	        return adminAssetRepo.findByAssetBrandOrderByAddedDateDesc(assetBrand);
//	    }
//		return null; 
//	}

//	@Override
//	public Page<AdminAsset> getAssets(String assetId, String assetCat, String assetSubCat, String assetModel,
//	                                  String assetBrand, int pageNo, int pageSize) {
//	    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("addedDate").descending());
//
//	    if (assetId != null) {
//	        return adminAssetRepo.findByAssetId(assetId, pageable);
//	    } else if (assetCat != null) {
//	        return adminAssetRepo.findByAssetCat(assetCat, pageable);
//	    } else if (assetSubCat != null) {
//	        return adminAssetRepo.findByAssetSubCat(assetSubCat, pageable);
//	    } else if (assetModel != null) {
//	        return adminAssetRepo.findByAssetModel(assetModel, pageable);
//	    } else if (assetBrand != null) {
//	        return adminAssetRepo.findByAssetBrand(assetBrand, pageable);
//	    }
//	    return Page.empty();
//	}

	@Override
	public Page<AdminAsset> getAssets(String assetId, String assetCat, String assetSubCat,
	                                  String assetModel, String assetBrand,
	                                  int pageNo, int pageSize) {

	    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("addedDate").descending());

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // ----------- MAIN QUERY -----------
	    CriteriaQuery<AdminAsset> query = cb.createQuery(AdminAsset.class);
	    Root<AdminAsset> root = query.from(AdminAsset.class);

	    List<Predicate> predicates = new ArrayList<>();
	    if (assetId != null && !assetId.isEmpty()) {
	        predicates.add(cb.equal(root.get("assetId"), assetId));
	    }
	    if (assetCat != null && !assetCat.isEmpty()) {
	        predicates.add(cb.equal(root.get("assetCat"), assetCat));
	    }
	    if (assetSubCat != null && !assetSubCat.isEmpty()) {
	        predicates.add(cb.equal(root.get("assetSubCat"), assetSubCat));
	    }
	    if (assetModel != null && !assetModel.isEmpty()) {
	        predicates.add(cb.equal(root.get("assetModel"), assetModel));
	    }
	    if (assetBrand != null && !assetBrand.isEmpty()) {
	        predicates.add(cb.equal(root.get("assetBrand"), assetBrand));
	    }

	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	    query.orderBy(cb.desc(root.get("addedDate")));

	    TypedQuery<AdminAsset> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    List<AdminAsset> assets = typedQuery.getResultList();

	    // ----------- COUNT QUERY -----------
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<AdminAsset> countRoot = countQuery.from(AdminAsset.class);

	    List<Predicate> countPredicates = new ArrayList<>();
	    if (assetId != null && !assetId.isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("assetId"), assetId));
	    }
	    if (assetCat != null && !assetCat.isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("assetCat"), assetCat));
	    }
	    if (assetSubCat != null && !assetSubCat.isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("assetSubCat"), assetSubCat));
	    }
	    if (assetModel != null && !assetModel.isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("assetModel"), assetModel));
	    }
	    if (assetBrand != null && !assetBrand.isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("assetBrand"), assetBrand));
	    }

	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(assets, pageable, totalCount);
	}


//	@Override
//	public List<AdminAsset> getAssetCivil(String assetCat) {
//
//		if ("CIVIL".equalsIgnoreCase(assetCat)) {
//			return adminAssetRepo.findByAssetCat(assetCat);
//		} else {
//			return List.of();
//		}
//
//	}

	@Override
	public Page<AdminAsset> getAssetCivil(String assetCat, int pageNo, int pageSize) {
	    if (!"CIVIL".equalsIgnoreCase(assetCat)) {
	        return Page.empty();
	    }

	    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("addedDate").descending());
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // Main query
	    CriteriaQuery<AdminAsset> cq = cb.createQuery(AdminAsset.class);
	    Root<AdminAsset> root = cq.from(AdminAsset.class);

	    Predicate predicate = cb.equal(cb.upper(root.get("assetCat")), assetCat.toUpperCase());
	    cq.where(predicate);
	    cq.orderBy(cb.desc(root.get("addedDate")));

	    TypedQuery<AdminAsset> query = entityManager.createQuery(cq);
	    query.setFirstResult((int) pageable.getOffset());
	    query.setMaxResults(pageable.getPageSize());

	    // Count query
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<AdminAsset> countRoot = countQuery.from(AdminAsset.class);
	    countQuery.select(cb.count(countRoot));
	    countQuery.where(cb.equal(cb.upper(countRoot.get("assetCat")), assetCat.toUpperCase()));

	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(query.getResultList(), pageable, total);
	}

	@Override
	public List<AdminAsset> getAssetNonCivil(String assetCat) {

		if (!"CIVIL".equalsIgnoreCase(assetCat)) {
			return adminAssetRepo.findByAssetCat(assetCat);
		} else {
			return List.of();
		}
	}

	@Override
	public AdminAsset updateAssets(UpdateAssetDto asset) {
		String assetId = asset.getAssetId();
		String assetCat = asset.getAssetCat();
		String assetSubCat = asset.getAssetSubCat();
		String assetModel = asset.getAssetModel();
		String assetBrand = asset.getAssetBrand();
		Optional<AdminAsset> adminAsset = Optional.of(adminAssetRepo.findByAssetId(assetId));
		if (adminAsset.isPresent()) {
			adminAsset.get().setAssetCat(assetCat);
			adminAsset.get().setAssetSubCat(assetSubCat);
			adminAsset.get().setAssetModel(assetModel);
			adminAsset.get().setAssetBrand(assetBrand);
//			adminAsset.get().setAssetId(assetBrand+"_"+assetSubCat);

			return adminAssetRepo.save(adminAsset.get());
		}
		return null;
	}

}
