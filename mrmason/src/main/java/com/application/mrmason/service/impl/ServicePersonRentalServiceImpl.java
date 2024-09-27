package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	public List<RentalAssetResponseDTO> getRentalReq(String userId, String assetId, String assetCat,
			String assetSubCat, String assetBrand,
			String assetModel, String availableLocation) {

		log.info(">>Service Logger getRentalReq()");

		List<ServicePersonAssetsEntity> assets = fetchAssets(userId, assetId, assetCat, assetSubCat, assetBrand,
				assetModel);

		if (assets.isEmpty()) {
			log.info("No assets found for the given criteria.");
			return Collections.emptyList();
		}

		List<String> assetIds = assets.stream()
				.map(ServicePersonAssetsEntity::getAssetId)
				.collect(Collectors.toList());

		List<ServicePersonRentalEntity> rentals = spRentRepo.searchRentals(userId, assetId, availableLocation,
				assetIds);

		return rentals.stream()
				.map(rental -> {
					ServicePersonAssetsEntity asset = assets.stream()
							.filter(a -> a.getAssetId().equals(rental.getAssetId()))
							.findFirst()
							.orElseThrow(
									() -> new RuntimeException("Asset not found for rental: " + rental.getAssetId()));

					return buildRentalAssetResponseDTO(rental, asset);
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<RentalAssetResponseDTO> getRentalAssets(String userId, String assetId, String assetCat,
			String assetSubCat, String assetBrand,
			String assetModel, String availableLocation) {

		log.info(">>Service Logger getRentalAssets()");

		List<ServicePersonAssetsEntity> assets = fetchAssets(userId, assetId, assetCat, assetSubCat, assetBrand,
				assetModel);

		if (assets.isEmpty()) {
			log.info("No assets found for the given criteria.");
			return Collections.emptyList();
		}

		List<String> assetIds = assets.stream()
				.map(ServicePersonAssetsEntity::getAssetId)
				.collect(Collectors.toList());

		List<ServicePersonRentalEntity> rentals = spRentRepo.searchRentals(userId, assetId, availableLocation,
				assetIds);

		return rentals.stream()
				.map(rental -> {
					ServicePersonAssetsEntity asset = assets.stream()
							.filter(a -> a.getAssetId().equals(rental.getAssetId()))
							.findFirst()
							.orElseThrow(
									() -> new RuntimeException("Asset not found for rental: " + rental.getAssetId()));

					return buildRentalAssetResponseDTO(rental, asset);
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<RentalAssetResponseDTO> getRentalAssetsNoAuth(String userId, String assetId, String assetCat,
			String assetSubCat, String assetBrand,
			String assetModel, String availableLocation) {

		log.info(">>Service Logger getRentalAssetsNoAuth()");

		List<ServicePersonAssetsEntity> assets = fetchAssets(userId, assetId, assetCat, assetSubCat, assetBrand,
				assetModel);

		if (assets.isEmpty()) {
			log.info("No assets found for the given criteria.");
			return Collections.emptyList();
		}

		List<String> assetIds = assets.stream()
				.map(ServicePersonAssetsEntity::getAssetId)
				.collect(Collectors.toList());

		List<ServicePersonRentalEntity> rentals = spRentRepo.searchRentals(userId, assetId, availableLocation,
				assetIds);

		return rentals.stream()
				.map(rental -> {
					ServicePersonAssetsEntity asset = assets.stream()
							.filter(a -> a.getAssetId().equals(rental.getAssetId()))
							.findFirst()
							.orElseThrow(
									() -> new RuntimeException("Asset not found for rental: " + rental.getAssetId()));

					return buildRentalAssetResponseDTO(rental, asset);
				})
				.collect(Collectors.toList());
	}

	private RentalAssetResponseDTO buildRentalAssetResponseDTO(ServicePersonRentalEntity rental,
			ServicePersonAssetsEntity asset) {
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
		return dto;
	}

	@Override
	public ServicePersonRentalEntity updateRentalAssetCharge(ServicePersonRentalEntity rent) {

		Optional<ServicePersonRentalEntity> user = spRentRepo.findByAssetIdAndUserId(rent.getAssetId(),
				rent.getUserId());

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

	private List<ServicePersonAssetsEntity> fetchAssets(String userId, String assetId, String assetCat,
			String assetSubCat,
			String assetBrand, String assetModel) {
		return spAssetRepo.searchAssets(userId, assetId, assetCat, assetSubCat, assetBrand, assetModel);
	}

}
