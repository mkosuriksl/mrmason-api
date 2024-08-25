package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ServicePersonAssetsDTO;
import com.application.mrmason.entity.ServicePersonAssetsEntity;

import com.application.mrmason.repository.ServicePersonAssetsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.ServicePersonAssetsService;

@Service

public class ServicePersonAssetsServiceImpl implements ServicePersonAssetsService {
    @Autowired
    ServicePersonAssetsRepo assetRepo;
    @Autowired
    UserDAO regiRepo;

    @Override
    public ServicePersonAssetsEntity saveAssets(ServicePersonAssetsEntity asset) {
        if (regiRepo.findByBodSeqNo(asset.getUserId()) != null) {
            return assetRepo.save(asset);
        }
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
    public List<ServicePersonAssetsEntity> getAssets(String userId, String assetId, String location, String assetCat,
            String assetSubCat, String assetModel, String assetBrand) {

        List<ServicePersonAssetsEntity> result = Collections.emptyList();

        if (userId != null) {
            if (assetCat != null && assetSubCat != null) {
                result = assetRepo.findByUserIdAndAssetCatAndAssetSubCat(userId, assetCat, assetSubCat);
            } else if (assetId != null && location != null) {
                result = assetRepo.findByUserIdAndAssetIdAndLocation(userId, assetId, location);
            } else if (location != null) {
                result = assetRepo.findByUserIdAndLocation(userId, location);
            } else if (assetId != null) {
                result = assetRepo.findByUserIdAndAssetId(userId, assetId).map(Collections::singletonList)
                        .orElse(Collections.emptyList());
            } else if (assetCat != null) {
                result = assetRepo.findByAssetCatAndUserId(assetCat, userId);
            } else if (assetSubCat != null) {
                result = assetRepo.findByAssetSubCatAndUserId(assetSubCat, userId);
            } else if (assetModel != null) {
                result = assetRepo.findByAssetModelAndUserId(assetModel, userId);
            } else if (assetBrand != null) {
                result = assetRepo.findByAssetBrandAndUserId(assetBrand, userId);
            } else {
                result = assetRepo.findByUserIdOrderByIdDesc(userId);
            }
        } else if (assetId != null) {
            result = assetRepo.findByAssetIdOrderByIdDesc(assetId);
        } else if (location != null) {
            result = assetRepo.findByLocationOrderByIdDesc(location);
        } else if (assetCat != null) {
            result = assetRepo.findByAssetCatOrderByIdDesc(assetCat);
        } else if (assetSubCat != null) {
            result = assetRepo.findByAssetSubCatOrderByIdDesc(assetSubCat);
        } else if (assetModel != null) {
            result = assetRepo.findByAssetModelOrderByIdDesc(assetModel);
        } else if (assetBrand != null) {
            result = assetRepo.findByAssetBrandOrderByIdDesc(assetBrand);
        }

        return result != null ? result : Collections.emptyList();
    }

    @Override
    public ServicePersonAssetsDTO getAssetByAssetId(String assetId) {
        if (assetRepo.findAllByAssetId(assetId) != null) {
            Optional<ServicePersonAssetsEntity> assetDb = assetRepo.findAllByAssetId(assetId);
            ServicePersonAssetsEntity assetData = assetDb.get();
            ServicePersonAssetsDTO assetDto = new ServicePersonAssetsDTO();

            assetDto.setAssetCat(assetData.getAssetCat());
            assetDto.setAssetSubCat(assetData.getAssetSubCat());
            assetDto.setDistrict(assetData.getDistrict());
            assetDto.setDoorNo(assetData.getDoorNo());
            assetDto.setLocation(assetData.getLocation());
            assetDto.setPinCode(assetData.getPinCode());
            assetDto.setState(assetData.getState());
            assetDto.setStreet(assetData.getStreet());
            assetDto.setTown(assetData.getTown());
            assetDto.setAssetModel(assetData.getAssetModel());
            assetDto.setRegDate(assetData.getRegDateFormatted());
            assetDto.setPlanId(assetData.getPlanId());
            assetDto.setMembershipExp(assetData.getMembershipExpDb());
            assetDto.setAssetId(assetData.getAssetId());
            assetDto.setUserId(assetData.getUserId());
            assetDto.setAssetBrand(assetData.getAssetBrand());
            return assetDto;

        }
        return null;
    }

}
