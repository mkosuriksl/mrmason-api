package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.RentalAssetResponseDTO;

import com.application.mrmason.entity.ServicePersonRentalEntity;

public interface ServicePersonRentalService {

	ServicePersonRentalEntity addRentalReq(ServicePersonRentalEntity rent);

	List<RentalAssetResponseDTO> getRentalReq(String assetCat, String assetSubCat, String assetBrand,
			String assetModel, String userId, String assetId, String availableLocation);

	List<RentalAssetResponseDTO> getRentalAssets(String assetCat, String assetSubCat, String assetBrand,
			String assetModel, String userId, String assetId);

	ServicePersonRentalEntity updateRentalAssetCharge(ServicePersonRentalEntity rent);

}
