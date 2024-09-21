package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.application.mrmason.dto.ServicePersonAssetsDTO;
import com.application.mrmason.entity.ServicePersonAssetsEntity;
import com.application.mrmason.repository.ServicePersonAssetsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.ServicePersonAssetsService;

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
        Optional<ServicePersonAssetsEntity> assetDb = assetRepo.findByUserIdAndAssetId(asset.getUserId(),
                asset.getAssetId());
        if (assetDb.isPresent()) {
            ServicePersonAssetsEntity user = assetDb.get();
            user.setAssetCat(asset.getAssetCat());
            user.setAssetSubCat(asset.getAssetSubCat());
            // ... (set other fields)
            log.info("Updating asset: {}", asset.getAssetId());
            return assetRepo.save(user);
        }
        log.warn("Asset not found for update: {}", asset.getAssetId());
        return null;
    }

    @Override
    public List<ServicePersonAssetsEntity> getAssets(String userId, String assetId, String location, String assetCat,
            String assetSubCat, String assetModel, String assetBrand) {

        log.info(
                "Fetching assets with filters: userId={}, assetId={}, assetCat={}, assetSubCat={}, assetBrand={}, assetModel={}",
                userId, assetId, assetCat, assetSubCat, assetBrand, assetModel);

        List<ServicePersonAssetsEntity> result = assetRepo.searchAssets(userId, assetId, assetCat, assetSubCat,
                assetBrand, assetModel);

        log.info("Number of assets found: {}", result.size());

        return result != null ? result : Collections.emptyList();
    }

    @Override
    public ServicePersonAssetsDTO getAssetByAssetId(String assetId) {
        Optional<ServicePersonAssetsEntity> assetDb = assetRepo.findAllByAssetId(assetId);
        if (assetDb.isPresent()) {
            ServicePersonAssetsEntity assetData = assetDb.get();
            ServicePersonAssetsDTO assetDto = new ServicePersonAssetsDTO();

            assetDto.setUserId(assetData.getUserId());
            assetDto.setAssetId(assetData.getAssetId());
            assetDto.setAssetCat(assetData.getAssetCat());
            assetDto.setAssetSubCat(assetData.getAssetSubCat());
            assetDto.setLocation(assetData.getLocation());
            assetDto.setStreet(assetData.getStreet());
            assetDto.setDoorNo(assetData.getDoorNo());
            assetDto.setTown(assetData.getTown());
            assetDto.setDistrict(assetData.getDistrict());
            assetDto.setState(assetData.getState());
            assetDto.setPinCode(assetData.getPinCode());
            assetDto.setAssetBrand(assetData.getAssetBrand());
            assetDto.setAssetModel(assetData.getAssetModel());
            assetDto.setRegDate(assetData.getRegDateFormatted());
            assetDto.setPlanId(assetData.getPlanId());
            assetDto.setMembershipExp(assetData.getMembershipExpDb());

            log.info("Fetched asset details for assetId: {}", assetId);
            return assetDto;
        }
        log.warn("No asset found for assetId: {}", assetId);
        return null;
    }

}
