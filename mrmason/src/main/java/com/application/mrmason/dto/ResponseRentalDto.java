package com.application.mrmason.dto;

import com.application.mrmason.entity.Rentel;

import lombok.Data;
@Data
public class ResponseRentalDto {
	private String message;
	private Rentel addRental; 
	
}
