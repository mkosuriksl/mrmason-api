package com.application.mrmason.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity

@Table(name = "c_material_request_header_details")
public class CMaterialReqHeaderDetailsEntity {

    @Id
    @Column(name = "c_mat_request_id_lineid")
    private String cMatRequestIdLineid;

    @Column(name = "c_mat_request_id")
    private String cMatRequestId;

    @Column(name = "material_category")
    private String materialCategory;

    @Column(name = "brand")
    private String brand;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_size")
    private String itemSize;

    @Column(name = "qty")
    private int qty;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "updated_date")
    private LocalDate updatedDate;
}
