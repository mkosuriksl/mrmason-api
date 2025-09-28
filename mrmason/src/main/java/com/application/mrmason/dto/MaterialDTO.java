package com.application.mrmason.dto;

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
    private String width;
    private String length;
    private String size;
    private String thickness;
}

