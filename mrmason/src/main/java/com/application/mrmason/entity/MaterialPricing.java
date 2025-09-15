package com.application.mrmason.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "material_pricing")
@Data
public class MaterialPricing {

    @Id
    private String userIdSku;  // primary key (same as material_master or linked)

    private Double mrp;
    private Double discount;
    private Double amount;
    private Double gst;

    private String updatedBy;
    private LocalDateTime updatedDate;
}

