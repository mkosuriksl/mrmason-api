package com.application.mrmason.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMaterialReqHeaderDetailsResponseDTO {

    @JsonProperty("cMatRequestIdLineid")
    private String cMatRequestIdLineid;

    @JsonProperty("cMatRequestId")
    private String cMatRequestId;

    @JsonProperty("materialCategory")
    private String materialCategory;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("itemName")
    private String itemName;

    @JsonProperty("itemSize")
    private String itemSize;

    @JsonProperty("qty")
    private int qty;

    @JsonProperty("updatedBy")
    private String updatedBy;

    @JsonProperty("updatedDate")
    private LocalDate updatedDate;
}
