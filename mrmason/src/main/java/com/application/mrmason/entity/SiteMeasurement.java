package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "site_measurement")
@AllArgsConstructor
@NoArgsConstructor
public class SiteMeasurement {
    
    @Id
    @Column(name = "service_request_id")
    private String serviceRequestId;
    
    @Column(name = "east_site_length")
    private String eastSiteLength;
    
    @Column(name = "west_site_length")
    private String westSiteLength;
    
    @Column(name = "south_site_length")
    private String southSiteLength;
    
    @Column(name = "north_site_length")
    private String northSiteLength;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "expected_bed_rooms")
    private String expectedBedRooms;
    
    @Column(name = "expected_attached_bath_rooms")
    private String expectedAttachedBathRooms;
    
    @Column(name = "expected_additional_bath_rooms")
    private String expectedAdditionalBathRooms;
    
    @Column(name = "expected_start_date")
    private Date expectedStartDate;
    
    @Column(name = "updatedDate")
    private Date updatedDate;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "customer_Id")
    private String customerId;
    
//    @Column(name = "user_id")
//    private String userId; 

    @Column(name = "building_type")
    private String buildingType;
    
    @Column(name = "no_of_floors")
    private String noOfFloors;
    
    @Column(name = "request_date")
    private Date requestDate;
    
    @Column(name = "status")
    private String status;
    
    @PrePersist
	private void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		String year = String.valueOf(now.getYear());
		String month = String.format("%02d", now.getMonthValue());
		String day = String.format("%02d", now.getDayOfMonth());
		String hour = String.format("%02d", now.getHour());
		String minute = String.format("%02d", now.getMinute());
		String second = String.format("%02d", now.getSecond());
		String millis = String.format("%03d", now.getNano() / 1000000).substring(0, 2);
		this.serviceRequestId = "SR" + year + month + day + hour + minute + second + millis;
		if (location != null) {
			// Remove unnecessary spaces and format commas properly
        	   location = location.replaceAll("\\s*,\\s*", ",").trim();
		}
	}
}
