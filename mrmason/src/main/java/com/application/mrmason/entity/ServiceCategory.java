package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name="admin_service_category")
@Builder
public class ServiceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="serviceCategory")
    private String serviceCategory;
    @Column(name="serviceSubCategory")
    private String serviceSubCategory;
    @Column(name="updated_date")
    private String updatedDate; 
    @Builder.Default
    @Column(name="updated_by")
    private String updatedBy = "none";
    @Column(name="added_by")
    private String addedBy;
    @Column(name="create_date")
    private String createDate; 
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createDate = now.format(formatter);
        this.updatedDate = now.format(formatter); 
    }

    @PreUpdate
    private void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        this.updatedDate = now.format(formatter); 
    }
}
