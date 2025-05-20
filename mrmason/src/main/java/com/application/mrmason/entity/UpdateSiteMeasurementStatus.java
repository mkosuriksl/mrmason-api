package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "update_site_measurement_status")
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSiteMeasurementStatus {
    
    @Id
    @Column(name = "service_request_id")
    private String serviceRequestId;
     
    @Column(name = "updatedDate")
    private Date updatedDate;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "update_status")
    private String status;
    
    @Column(name = "comments")
    private String comments;
   
}
