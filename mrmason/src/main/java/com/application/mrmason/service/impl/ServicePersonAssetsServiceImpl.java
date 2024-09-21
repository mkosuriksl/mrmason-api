package com.application.mrmason.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ServicePersonAssetsDTO;
import com.application.mrmason.entity.ServicePersonAssetsEntity;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.ServicePersonAssetsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.ServicePersonAssetsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServicePersonAssetsServiceImpl implements ServicePersonAssetsService {
	@Autowired
	ServicePersonAssetsRepo assetRepo;
	@Autowired
	UserDAO regiRepo;

	@Override
	public ServicePersonAssetsEntity saveAssets(ServicePersonAssetsEntity asset) {
		if (regiRepo.findByBodSeqNo(asset.getUserId()) != null) {
			log.info("Saving asset: {}", asset.getAssetId());
			return assetRepo.save(asset);
		}
		log.warn("User not found for asset: {}", asset.getAssetId());
		return null;
	}

	@Override
	public ServicePersonAssetsEntity updateAssets(ServicePersonAssetsDTO asset) {
		ServicePersonAssetsEntity assetDb = assetRepo.findByUserIdAndAssetId(asset.getUserId(), asset.getAssetId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Service PersonAssets Details not found by : " + asset.getUserId() + " " + asset.getAssetId()));
		assetDb.setAssetCat(asset.getAssetCat());
		assetDb.setAssetSubCat(asset.getAssetSubCat());
		// ... (set other fields)
		log.info("Updating asset: {}", asset.getAssetId());
		return assetRepo.save(assetDb);
	}

	@Override
	public List<ServicePersonAssetsEntity> getAssets(String userId, String assetId, String location, String assetCat,
			String assetSubCat, String assetModel, String assetBrand) {
		log.info("getAssets({}, {}, {}, {}, {}, {})", userId, assetId, assetCat, assetSubCat, assetBrand, assetModel);
		return assetRepo.searchAssets(userId, assetId, assetCat, assetSubCat, assetBrand, assetModel);

	}

	@Override
	public ServicePersonAssetsDTO getAssetByAssetId(String assetId) {
		log.info("Fetched asset details for assetId({})", assetId);
		ServicePersonAssetsEntity assetDb = assetRepo.findAllByAssetId(assetId).orElseThrow(
				() -> new ResourceNotFoundException("Service PersonAssets Details not found by : " + assetId));
		ServicePersonAssetsDTO assetDto = new ServicePersonAssetsDTO();
		assetDto.setUserId(assetDb.getUserId());
		assetDto.setAssetId(assetDb.getAssetId());
		assetDto.setAssetCat(assetDb.getAssetCat());
		assetDto.setAssetSubCat(assetDb.getAssetSubCat());
		assetDto.setLocation(assetDb.getLocation());
		assetDto.setStreet(assetDb.getStreet());
		assetDto.setDoorNo(assetDb.getDoorNo());
		assetDto.setTown(assetDb.getTown());
		assetDto.setDistrict(assetDb.getDistrict());
		assetDto.setState(assetDb.getState());
		assetDto.setPinCode(assetDb.getPinCode());
		assetDto.setAssetBrand(assetDb.getAssetBrand());
		assetDto.setAssetModel(assetDb.getAssetModel());
		assetDto.setRegDate(assetDb.getRegDateFormatted());
		assetDto.setPlanId(assetDb.getPlanId());
		assetDto.setMembershipExp(assetDb.getMembershipExpDb());
		return assetDto;
	}

}
