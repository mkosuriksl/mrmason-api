package com.application.mrmason.service;

import java.util.List;

//import com.application.mrmason.dto.RentalDto;
import com.application.mrmason.entity.Rental;

public interface RentalService {
	Rental addRentalReq(Rental rent);
	List<Rental> getRentalReq(String assetId,String userId);
	Rental updateRentalReq(Rental rent);
	
	List<Rental> getRentalAssets(String assetCat, String assetSubCat, String assetBrand, String assetModel, String userId);
	
    Rental updateRentalAssetCharge(String assetId,String userId, String isAvailRent,String amountPerDay, 
	String amountper30days, String pickup,String availableLocation,String delivery);

	
}
