package com.application.mrmason.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequirementByRequestDTO {

	private String reqIdLineId;

	private String materialCategory;

	private String brand;

	private String itemName;

	private String shape;

	private String modelName;

	private String modelCode;

	private BigDecimal sizeInInch;

	private BigDecimal length;

	private String lengthInUnit;

	private BigDecimal width;

	private String widthInUnit;

	private BigDecimal thickness;

	private String thicknessInUnit;

	private Integer noOfItems;

	private BigDecimal weightInKgs;

	private String reqId; 

	private BigDecimal amount;

	private BigDecimal gst;

	private BigDecimal totalAmount;

	private String spId;

	private String updatedBy;

	private Date updatedDate;

	private String status;
}