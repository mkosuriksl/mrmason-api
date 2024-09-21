package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

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
		Optional<ServicePersonAssetsEntity> assetDb = assetRepo.findAllByAssetId(assetId);
		if (assetDb.isEmpty()) {
			return null;
		}
		ServicePersonAssetsDTO assetDto = new ServicePersonAssetsDTO();
		assetDto.setUserId(assetDb.get().getUserId());
		assetDto.setAssetId(assetDb.get().getAssetId());
		assetDto.setAssetCat(assetDb.get().getAssetCat());
		assetDto.setAssetSubCat(assetDb.get().getAssetSubCat());
		assetDto.setLocation(assetDb.get().getLocation());
		assetDto.setStreet(assetDb.get().getStreet());
		assetDto.setDoorNo(assetDb.get().getDoorNo());
		assetDto.setTown(assetDb.get().getTown());
		assetDto.setDistrict(assetDb.get().getDistrict());
		assetDto.setState(assetDb.get().getState());
		assetDto.setPinCode(assetDb.get().getPinCode());
		assetDto.setAssetBrand(assetDb.get().getAssetBrand());
		assetDto.setAssetModel(assetDb.get().getAssetModel());
		assetDto.setRegDate(assetDb.get().getRegDateFormatted());
		assetDto.setPlanId(assetDb.get().getPlanId());
		assetDto.setMembershipExp(assetDb.get().getMembershipExpDb());
		return assetDto;
	}

}
