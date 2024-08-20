package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.application.mrmason.entity.CustomerAssets;
import com.application.mrmason.entity.Rental;
import com.application.mrmason.repository.CustomerAssetsRepo;
import com.application.mrmason.repository.RentalRepo;
import com.application.mrmason.service.RentalService;

@Service
public class RentalServiceImpl implements RentalService{
	@Autowired
	public RentalRepo rentRepo;
	@Autowired
	public CustomerAssetsRepo assetRepo;
	
	@Override
	public Rental addRentalReq(Rental rent) {
		Optional<CustomerAssets> user=assetRepo.findByUserIdAndAssetId(rent.getUserId(),rent.getAssetId());
		if(user.isPresent()) {
			return rentRepo.save(rent);
		}
		return null;
	}
	@Override
	public List<Rental> getRentalReq(String assetId,String userId) {
		Optional<List<Rental>> rentUser=Optional.of(rentRepo.findByAssetIdOrUserId(assetId, userId));
		if(rentUser.isPresent()) {
			
			return rentUser.get();
		}
		return null;
	}
	@Override
	public Rental updateRentalReq(Rental rent) {
		Optional<Rental> user=Optional.of(rentRepo.findByAssetIdAndUserId(rent.getAssetId(),rent.getUserId()));
		if(user.isPresent()) {
			Rental rentUser=user.get();
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
	@Override
    public List<Rental> getRentalAssets(String assetCat, String assetSubCat, String assetBrand, String assetModel,String userId) {
		
        // Customer Assets will be fetched based on the given parameters
        List<CustomerAssets> assets = assetRepo. findByAssetCatAndAssetSubCatAndAssetBrandAndAssetModelAndUserId(
			assetCat, assetSubCat, assetBrand, assetModel, userId);


        // Collect assetIds from CustomerAssets and fetch corresponding Rentel records
        List<String> assetIds = assets.stream()
                .map(CustomerAssets::getAssetId)
                .toList();
		

    return rentRepo.findByAssetIdIn(assetIds);
    }

    @Override
    public Rental updateRentalAssetCharge(String assetId,String userId, String isAvailRent,String amountPerDay, 
	String amountper30days, String pickup,String availableLocation,String delivery) {
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
