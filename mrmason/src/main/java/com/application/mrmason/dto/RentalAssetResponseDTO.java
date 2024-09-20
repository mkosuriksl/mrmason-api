package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RentalAssetResponseDTO {

    private String userId;
    private String assetId;
    private String assetCat;
    private String assetSubCat;
    private String assetBrand;
    private String assetModel;
    private String isAvailRent;
    private String amountPerDay;
    private String amountPer30days;
    private String pickup;
    private String availableLocation;
    private String delivery;
    private String updateDate;

}
