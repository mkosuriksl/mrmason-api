package com.application.mrmason.service;

import java.util.List;
import com.application.mrmason.dto.UpdateAssetDto;
import com.application.mrmason.entity.AdminAsset;


public interface AdminAssetService {
	AdminAsset addAdminAssets(AdminAsset asset);
	List<AdminAsset> getAssets(UpdateAssetDto updateDto);
	AdminAsset updateAssets(UpdateAssetDto asset);
}
