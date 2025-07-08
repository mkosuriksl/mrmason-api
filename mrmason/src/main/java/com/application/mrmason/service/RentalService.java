package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;

//import com.application.mrmason.dto.RentalDto;
import com.application.mrmason.entity.Rental;

public interface RentalService {
	Rental addRentalReq(Rental rent);
//	List<Rental> getRentalReq(String assetId,String userId);
	public Page<Rental> getRentalReq(String assetId, String userId, int page, int size);
	Rental updateRentalReq(Rental rent);
	
//	List<Rental> getRentalAssets(String assetCat, String assetSubCat, String assetBrand, String assetModel, String userId);
	public Page<Rental> getRentalAssets(String assetCat, String assetSubCat, String assetBrand, String assetModel,
            String userId, int page, int size);
    Rental updateRentalAssetCharge(String assetId,String userId, String isAvailRent,String amountPerDay, 
	String amountper30days, String pickup,String availableLocation,String delivery);

	
}
