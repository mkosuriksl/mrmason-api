package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.Rental;

import lombok.Data;
@Data
public class ResponseListRentalDto {
	private String message;
	private boolean status;
	private List<Rental> data;
}
