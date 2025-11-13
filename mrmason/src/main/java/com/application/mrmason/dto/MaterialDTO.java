package com.application.mrmason.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDTO {
    private String skuId;
    private String modelNo;
    private String modelName;
    private String shape;
    private BigDecimal width;
    private BigDecimal length;
    private BigDecimal size;
    private BigDecimal thickness;
}

