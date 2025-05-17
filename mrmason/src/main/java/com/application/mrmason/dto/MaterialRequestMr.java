package com.application.mrmason.dto;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "material_request_mr")
@Data
public class MaterialRequestMr {

    @Id
    private String reqIdLineId;

    private String reqId;

    private Date createdDate = new Date(); // Optional audit field

    public MaterialRequestMr() {}

    public MaterialRequestMr(String reqId, String reqIdLineId) {
        this.reqId = reqId;
        this.reqIdLineId = reqIdLineId;
        this.createdDate = new Date();
    }
}
