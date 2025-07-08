package com.application.mrmason.dto;

import com.application.mrmason.entity.ServicePersonRentalEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseSPRentalDTO {
	private String message;
	private boolean status;
	private ServicePersonRentalEntity addRental;
	private List<RentalAssetResponseDTO> rentalData;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
