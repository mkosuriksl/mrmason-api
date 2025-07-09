package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.application.mrmason.dto.RentalAssetResponseDTO;
import com.application.mrmason.entity.ServicePersonRentalEntity;

public interface ServicePersonRentalService {

	ServicePersonRentalEntity addRentalReq(ServicePersonRentalEntity rent);

//	List<RentalAssetResponseDTO> getRentalReq(String userId, String assetId, String assetCat,
//			String assetSubCat, String assetBrand,
//			String assetModel, String availableLocation);
	
	public Page<RentalAssetResponseDTO> getRentalReq(String userId, String assetId, String assetCat,
            String assetSubCat, String assetBrand,
            String assetModel, String availableLocation,
            int page, int size);

	List<RentalAssetResponseDTO> getRentalAssets(String userId, String assetId, String assetCat,
			String assetSubCat, String assetBrand,
			String assetModel, String availableLocation);

	List<RentalAssetResponseDTO> getRentalAssetsNoAuth(String assetCat,
			String assetSubCat, String assetBrand,
			String assetModel, String availableLocation);

	ServicePersonRentalEntity updateRentalAssetCharge(ServicePersonRentalEntity rent);

}
