package com.application.mrmason.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_services")
@Builder
public class SpServiceDetails {
	@Id
	private String userServicesId;
	private String serviceType;
	private String userId;
	private String qualification;
	private String experience;
	private String charge;
	private String certificate1;
	private String certificate2;
	@Builder.Default
	private String status="active";
	private String availableWithinRange;
	@Column(name="pincode")
	private String location;
	private String city;
	
	@PrePersist
	private void prePersist() {
	
        this.userServicesId =serviceType+"_"+userId;
        if (location != null) {
			// Remove unnecessary spaces and format commas properly
        	 location = location.replaceAll("\\s*,\\s*", ",").trim();

             // Remove spaces within each part of the comma-separated location
             String[] parts = location.split(",");
             for (int i = 0; i < parts.length; i++) {
                 parts[i] = parts[i].replaceAll("\\s+", "");
             }
             location = String.join(",", parts);
		}
	}
}
