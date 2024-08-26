package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ServicePersonAssetsDTO;
import com.application.mrmason.entity.ServicePersonAssetsEntity;

public interface ServicePersonAssetsService {
    ServicePersonAssetsEntity saveAssets(ServicePersonAssetsEntity asset);

    ServicePersonAssetsEntity updateAssets(ServicePersonAssetsDTO asset);

    List<ServicePersonAssetsEntity> getAssets(String userId, String assetId, String location, String assetCat,
            String assetSubCat, String assetModel, String assetBrand);

    ServicePersonAssetsDTO getAssetByAssetId(String assetId);

}
