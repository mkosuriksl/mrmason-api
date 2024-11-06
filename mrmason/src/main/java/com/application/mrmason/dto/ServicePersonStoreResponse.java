package com.application.mrmason.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ServicePersonStoreResponse {

	private String spUserId;

	private String storeId;

	private String spUserIdStoreId;

	private String location;

	private String gst;

	private String gstDocument;

	private String tradeLicense;

	private String updatedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss.SSSXXX", timezone = "Asia/Kolkata")
	private Timestamp updatedDate;
}
