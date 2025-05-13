package com.application.mrmason.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "material_requirement_by_Request")
public class MaterialRequirementByRequest {

    @Id
    @Column(name = "req_id_line_id", unique = true)
    private String reqIdLineId;

    @Column(name = "material_category")
    private String materialCategory;

    @Column(name = "brand")
    private String brand;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "shape")
    private String shape;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "model_code")
    private String modelCode;

    @Column(name = "size_in_inch")
    private BigDecimal sizeInInch;

    @Column(name = "length")
    private BigDecimal length;

    @Column(name = "length_in_unit")
    private String lengthInUnit;

    @Column(name = "width")
    private BigDecimal width;

    @Column(name = "width_in_unit")
    private String widthInUnit;

    @Column(name = "thickness")
    private BigDecimal thickness;

    @Column(name = "thickness_in_unit")
    private String thicknessInUnit;

    @Column(name = "no_of_items")
    private Integer noOfItems;

    @Column(name = "weight_in_kgs")
    private BigDecimal weightInKgs;

    @Column(name = "request_id")
    private String reqId;  // Adjust based on actual FK type in DB

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "gst")
    private BigDecimal gst;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "sp_id")
    private String spId;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "status")
    private String status; 
}
