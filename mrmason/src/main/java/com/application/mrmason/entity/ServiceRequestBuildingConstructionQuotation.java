package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "service_request_building_construction_quotation")
public class ServiceRequestBuildingConstructionQuotation {
	
	@Id
	@Column(name = "request_lineid")
	private String requestLineId;

	@Column(name = "request_lineid_description")
	private String requestLineIdDescription;

	@Column(name = "quotation_id")
	private String requestId;

	@Column(name = "areas_in_sqft")
	private String areasInSqft;

	@Column(name = "quotation_amount")
	private Integer quotationAmount;

	@Column(name = "quoted_date")
	private Date quotedDate;

	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private SPWAStatus status;

	@Column(name = "no_of_days")
	private Integer noOfDays;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "sp_id")
	private String spId;

	@Column(name = "no_of_resources")
	private String noOfResources;
	
	 @PrePersist
	    private void prePersist() {
	        if (this.requestId == null) {
	            throw new IllegalStateException("requestId (quotationId) must be set before persisting!");
	        }
	        if (this.requestLineIdDescription == null || this.requestLineIdDescription.isEmpty()) {
	            throw new IllegalStateException("requestLineIdDescription must not be null or empty!");
	        }
	        if (this.requestLineId == null) {
	            this.requestLineId = this.requestId + "_" + this.requestLineIdDescription.replaceAll("\\s+", "").toUpperCase();
	        }
	    }

}
