package com.application.mrmason.dto;

import com.application.mrmason.entity.Rental;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.Data;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseRentalDto {
	private String message;
	private boolean status;
	private Rental addRental; 
	private List<Rental> rentalData;
}
