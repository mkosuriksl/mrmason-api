package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.entity.Rental;
import com.application.mrmason.repository.CustomerAssetsRepo;
import com.application.mrmason.repository.RentalRepo;
import com.application.mrmason.service.RentalService;

@Service
public class RentalServiceImpl implements RentalService {
	@Autowired
	public RentalRepo rentRepo;
	@Autowired
	public CustomerAssetsRepo assetRepo;

	@Override
	public Rental addRentalReq(Rental rent) {
		Optional<CustomerAssets> user = assetRepo.findByUserIdAndAssetId(rent.getUserId(), rent.getAssetId());
		if (user.isPresent()) {
			return rentRepo.save(rent);
		}
		return null;
	}

//	@Override
//	public List<Rental> getRentalReq(String assetId, String userId) {
//		Optional<List<Rental>> rentUser = Optional.of(rentRepo.findByAssetIdOrUserId(assetId, userId));
//		if (rentUser.isPresent()) {
//
//			return rentUser.get();
//		}
//		return null;
//	}
	
	@Override
	public Page<Rental> getRentalReq(String assetId, String userId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());

	    if (assetId != null && userId != null) {
	        return rentRepo.findByAssetIdAndUserId(assetId, userId, pageable);
	    } else if (assetId != null) {
	        return rentRepo.findByAssetId(assetId, pageable);
	    } else if (userId != null) {
	        return rentRepo.findByUserId(userId, pageable);
	    } else {
	        return rentRepo.findAll(pageable); // fallback to all data
	    }
	}


	@Override
	public Rental updateRentalReq(Rental rent) {
		Optional<Rental> user = Optional.of(rentRepo.findByAssetIdAndUserId(rent.getAssetId(), rent.getUserId()));
		if (user.isPresent()) {
			Rental rentUser = user.get();
			rentUser.setAmountper30days(rent.getAmountper30days());
			rentUser.setAmountPerDay(rent.getAmountPerDay());
			rentUser.setAvailableLocation(rent.getAvailableLocation());
			rentUser.setDelivery(rent.getDelivery());
			rentUser.setPickup(rent.getPickup());
			rentUser.setIsAvailRent(rent.getIsAvailRent());

			return rentRepo.save(rentUser);
		}
		return null;
	}

//	@Override
//	public List<Rental> getRentalAssets(String assetCat, String assetSubCat, String assetBrand, String assetModel,
//			String userId) {
//		List<CustomerAssets> assets;
//
//		if (assetBrand != null && !assetBrand.isEmpty() && assetModel != null && !assetModel.isEmpty()) {
//			assets = assetRepo.findByAssetCatAndAssetSubCatAndAssetBrandAndAssetModelAndUserId(assetCat, assetSubCat,
//					assetBrand, assetModel, userId);
//		} else if (assetBrand != null && !assetBrand.isEmpty()) {
//			assets = assetRepo.findByAssetCatAndAssetSubCatAndAssetBrandAndUserId(assetCat, assetSubCat, assetBrand,
//					userId);
//		} else if (assetModel != null && !assetModel.isEmpty()) {
//			assets = assetRepo.findByAssetCatAndAssetSubCatAndAssetModelAndUserId(assetCat, assetSubCat, assetModel,
//					userId);
//		} else {
//			assets = assetRepo.findByAssetCatAndAssetSubCatAndUserId(assetCat, assetSubCat, userId);
//		}
//		List<String> assetIds = assets.stream()
//				.map(CustomerAssets::getAssetId)
//				.toList();
//		return rentRepo.findByAssetIdIn(assetIds);
//	}
	
	@Override
	public Page<Rental> getRentalAssets(String assetCat, String assetSubCat, String assetBrand, String assetModel,
	                                    String userId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("assetId").descending());

	    List<CustomerAssets> assets;
	    if (assetBrand != null && !assetBrand.isEmpty() && assetModel != null && !assetModel.isEmpty()) {
	        assets = assetRepo.findByAssetCatAndAssetSubCatAndAssetBrandAndAssetModelAndUserId(
	                assetCat, assetSubCat, assetBrand, assetModel, userId);
	    } else if (assetBrand != null && !assetBrand.isEmpty()) {
	        assets = assetRepo.findByAssetCatAndAssetSubCatAndAssetBrandAndUserId(
	                assetCat, assetSubCat, assetBrand, userId);
	    } else if (assetModel != null && !assetModel.isEmpty()) {
	        assets = assetRepo.findByAssetCatAndAssetSubCatAndAssetModelAndUserId(
	                assetCat, assetSubCat, assetModel, userId);
	    } else {
	        assets = assetRepo.findByAssetCatAndAssetSubCatAndUserId(assetCat, assetSubCat, userId);
	    }

	    List<String> assetIds = assets.stream()
	            .map(CustomerAssets::getAssetId)
	            .toList();

	    return rentRepo.findByAssetIdIn(assetIds, pageable);
	}


	@Override
	public Rental updateRentalAssetCharge(String assetId, String userId, String isAvailRent, String amountPerDay,
			String amountper30days, String pickup, String availableLocation, String delivery) {
		Optional<Rental> existingRental = rentRepo.findById(assetId);

		if (existingRental.isPresent()) {
			Rental rental = existingRental.get();
			rental.setIsAvailRent(isAvailRent);
			rental.setAmountPerDay(amountPerDay);
			rental.setAmountper30days(amountper30days);
			rental.setPickup(pickup);
			rental.setAvailableLocation(availableLocation);
			rental.setDelivery(delivery);
			return rentRepo.save(rental);
		}
		return null;
	}

}
