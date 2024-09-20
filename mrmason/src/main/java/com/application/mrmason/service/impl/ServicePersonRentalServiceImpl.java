package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.RentalAssetResponseDTO;
import com.application.mrmason.entity.ServicePersonAssetsEntity;
import com.application.mrmason.entity.ServicePersonRentalEntity;
import com.application.mrmason.repository.ServicePersonAssetsRepo;
import com.application.mrmason.repository.ServicePersonRentalRepo;
import com.application.mrmason.service.ServicePersonRentalService;

@Service
public class ServicePersonRentalServiceImpl implements ServicePersonRentalService {

	@Autowired
	public ServicePersonRentalRepo spRentRepo;
	@Autowired
	public ServicePersonAssetsRepo spAssetRepo;

	@Override
	public ServicePersonRentalEntity addRentalReq(ServicePersonRentalEntity rent) {
		Optional<ServicePersonAssetsEntity> user = spAssetRepo.findByUserIdAndAssetId(rent.getUserId(),
				rent.getAssetId());
		if (user.isPresent()) {
			return spRentRepo.save(rent);
		}
		return null;
	}

	@Override
	public List<RentalAssetResponseDTO> getRentalReq(String assetCat, String assetSubCat, String assetBrand,
			String assetModel, String userId, String assetId, String availableLocation) {

		List<ServicePersonAssetsEntity> assets = Collections.emptyList();

		if (userId != null) {
			assets = fetchAssets(userId, assetCat, assetSubCat, assetBrand, assetModel, assetId);
		}

		List<ServicePersonRentalEntity> rentals = new ArrayList<>();

		List<String> assetIds = assets.stream()
				.map(ServicePersonAssetsEntity::getAssetId)
				.collect(Collectors.toList());

		if (userId != null && availableLocation != null) {
			rentals = spRentRepo.findByUserIdAndAvailableLocation(userId, availableLocation);
		}

		else if (availableLocation != null) {
			rentals = spRentRepo.findByAvailableLocation(availableLocation);
		}

		else if (!assetIds.isEmpty()) {
			rentals = spRentRepo.findByAssetIdIn(assetIds);
		}

		List<RentalAssetResponseDTO> responseDTOs = new ArrayList<>();

		for (ServicePersonRentalEntity rental : rentals) {

			ServicePersonAssetsEntity asset = assets.stream()
					.filter(a -> a.getAssetId().equals(rental.getAssetId()))
					.findFirst()
					.orElse(null);

			RentalAssetResponseDTO dto = new RentalAssetResponseDTO();
			dto.setAssetId(rental.getAssetId());
			dto.setUserId(rental.getUserId());
			dto.setIsAvailRent(rental.getIsAvailRent());
			dto.setAmountPerDay(rental.getAmountPerDay());
			dto.setAmountPer30days(rental.getAmountper30days());
			dto.setPickup(rental.getPickup());
			dto.setAvailableLocation(rental.getAvailableLocation());
			dto.setDelivery(rental.getDelivery());
			dto.setUpdateDate(rental.getUpdateDate());

			if (asset != null) {
				dto.setAssetCat(asset.getAssetCat());
				dto.setAssetSubCat(asset.getAssetSubCat());
				dto.setAssetBrand(asset.getAssetBrand());
				dto.setAssetModel(asset.getAssetModel());
			}

			responseDTOs.add(dto);
		}

		return responseDTOs;
	}

	private List<ServicePersonAssetsEntity> fetchAssets(String userId, String assetCat, String assetSubCat,
			String assetBrand, String assetModel, String assetId) {

		if (assetId != null) {
			return spAssetRepo.findByAssetId(assetId);
		} else if (assetCat != null && assetSubCat != null && assetBrand != null && assetModel != null) {
			return spAssetRepo.findByUserIdAndAssetCatAndAssetSubCatAndAssetBrandAndAssetModel(userId, assetCat,
					assetSubCat, assetBrand, assetModel);
		} else if (assetCat != null && assetSubCat != null) {
			return spAssetRepo.findByUserIdAndAssetCatAndAssetSubCat(userId, assetCat, assetSubCat);
		} else if (assetBrand != null && assetModel != null) {
			return spAssetRepo.findByUserIdAndAssetBrandAndAssetModel(userId, assetBrand, assetModel);
		} else if (assetCat != null) {
			return spAssetRepo.findByUserIdAndAssetCat(userId, assetCat);
		} else if (assetSubCat != null) {
			return spAssetRepo.findByUserIdAndAssetSubCat(userId, assetSubCat);
		} else if (assetBrand != null) {
			return spAssetRepo.findByUserIdAndAssetBrand(userId, assetBrand);
		} else if (assetModel != null) {
			return spAssetRepo.findByUserIdAndAssetModel(userId, assetModel);
		} else {
			return spAssetRepo.findByUserId(userId);
		}
	}

	@Override
	public List<RentalAssetResponseDTO> getRentalAssets(String assetCat, String assetSubCat, String assetBrand,
			String assetModel,
			String userId, String assetId) {

		List<ServicePersonAssetsEntity> assets = Collections.emptyList();

		if (userId != null) {
			if (assetId != null) {
				assets = spAssetRepo.findByAssetId(assetId);
			} else if (assetCat != null && assetSubCat != null && assetBrand != null && assetModel != null) {
				assets = spAssetRepo.findByUserIdAndAssetCatAndAssetSubCatAndAssetBrandAndAssetModel(userId, assetCat,
						assetSubCat, assetBrand, assetModel);
			} else if (assetCat != null && assetSubCat != null) {
				assets = spAssetRepo.findByUserIdAndAssetCatAndAssetSubCat(userId, assetCat, assetSubCat);
			} else if (assetBrand != null && assetModel != null) {
				assets = spAssetRepo.findByUserIdAndAssetBrandAndAssetModel(userId, assetBrand, assetModel);
			} else if (assetCat != null) {
				assets = spAssetRepo.findByUserIdAndAssetCat(userId, assetCat);
			} else if (assetSubCat != null) {
				assets = spAssetRepo.findByUserIdAndAssetSubCat(userId, assetSubCat);
			} else if (assetBrand != null) {
				assets = spAssetRepo.findByUserIdAndAssetBrand(userId, assetBrand);
			} else if (assetModel != null) {
				assets = spAssetRepo.findByUserIdAndAssetModel(userId, assetModel);
			}

			else {
				assets = spAssetRepo.findByUserId(userId);
			}
		}

		List<String> assetIds = assets.stream()
				.map(ServicePersonAssetsEntity::getAssetId)
				.toList();

		List<ServicePersonRentalEntity> rentals = spRentRepo.findByAssetIdIn(assetIds);

		List<RentalAssetResponseDTO> responseDTOs = new ArrayList<>();

		for (ServicePersonRentalEntity rental : rentals) {

			ServicePersonAssetsEntity asset = assets.stream()
					.filter(a -> a.getAssetId().equals(rental.getAssetId()))
					.findFirst()
					.orElse(null);

			if (asset != null) {

				RentalAssetResponseDTO dto = new RentalAssetResponseDTO();
				dto.setAssetId(rental.getAssetId());
				dto.setUserId(rental.getUserId());
				dto.setIsAvailRent(rental.getIsAvailRent());
				dto.setAmountPerDay(rental.getAmountPerDay());
				dto.setAmountPer30days(rental.getAmountper30days());
				dto.setPickup(rental.getPickup());
				dto.setAvailableLocation(rental.getAvailableLocation());
				dto.setDelivery(rental.getDelivery());
				dto.setUpdateDate(rental.getUpdateDate());

				dto.setAssetCat(asset.getAssetCat());
				dto.setAssetSubCat(asset.getAssetSubCat());
				dto.setAssetBrand(asset.getAssetBrand());
				dto.setAssetModel(asset.getAssetModel());

				responseDTOs.add(dto);
			}
		}

		return responseDTOs;
	}

	@Override
	public ServicePersonRentalEntity updateRentalAssetCharge(ServicePersonRentalEntity rent) {
		Optional<ServicePersonRentalEntity> user = Optional
				.of(spRentRepo.findByAssetIdAndUserId(rent.getAssetId(), rent.getUserId()));
		if (user.isPresent()) {
			ServicePersonRentalEntity rentUser = user.get();
			rentUser.setAmountper30days(rent.getAmountper30days());
			rentUser.setAmountPerDay(rent.getAmountPerDay());
			rentUser.setAvailableLocation(rent.getAvailableLocation());
			rentUser.setDelivery(rent.getDelivery());
			rentUser.setPickup(rent.getPickup());
			rentUser.setIsAvailRent(rent.getIsAvailRent());

			return spRentRepo.save(rentUser);
		}
		return null;
	}

}