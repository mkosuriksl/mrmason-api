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
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerAssetsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
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
	CustomerRegistrationRepo regiRepo;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	public AdminDetailsRepo adminRepo;
	@Autowired
	private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;
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
	public CustomerAssets updateAssets(CustomerAssetDto asset,RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		Optional<CustomerAssets> assetDb = assetRepo.findByUserIdAndAssetId(userInfo.userId, asset.getAssetId());
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
	public CustomerAssetDto getAssetByAssetId(CustomerAssets asset, RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		
		asset.setUserId(userInfo.userId);

	    // Save the entity
	    CustomerAssets savedAsset = assetRepo.save(asset);
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
		assetDto.setUserId(savedAsset.getUserId());
		assetDto.setAssetBrand(asset.getAssetBrand());
		assetRepo.save(asset);
		return assetDto;

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

	    List<String> roleNames = loggedInRole.stream()
	            .map(GrantedAuthority::getAuthority)
	            .map(role -> role.replace("ROLE_", ""))
	            .collect(Collectors.toList());

	    if (roleNames.isEmpty()) {
	        throw new ResourceNotFoundException("No roles assigned for user: " + loggedInUserEmail);
	    }

	    UserType userType = UserType.valueOf(roleNames.get(0));
	    String userId;

	    switch (userType) {
	        case Adm:
	            AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, UserType.Adm)
	                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
	            userId = admin.getEmail();
	            break;

	        case MS:
	            MaterialSupplierQuotationUser msUser = materialSupplierQuotationUserDAO
	                    .findByEmailAndUserTypeAndRegSource(loggedInUserEmail, UserType.MS, regSource)
	                    .orElseThrow(() -> new ResourceNotFoundException("Material User not found: " + loggedInUserEmail));
	            userId = msUser.getBodSeqNo();
	            break;

	        case EC:
	        case Developer:
	            // Try CustomerRegistration first
	            CustomerRegistration customer = regiRepo
	                    .findByUserEmailAndUserTypeAndRegSource(loggedInUserEmail, UserType.EC, regSource)
	                    .orElse(null);

	            if (customer != null) {
	                userId = customer.getUserid();
	            } else {
	                // If not found in Customer, try User table
	                User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, UserType.Developer, regSource)
	                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
	                userId = user.getBodSeqNo();
	            }
	            break;

	        default:
	            throw new ResourceNotFoundException("Unsupported user type: " + userType);
	    }

	    return new UserInfo(userId);
	}

}
