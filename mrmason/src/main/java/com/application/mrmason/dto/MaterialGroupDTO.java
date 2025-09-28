package com.application.mrmason.dto;

import java.util.List;

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
public class MaterialGroupDTO {
    private String materialCategory;
    private String materialSubCategory;
    private String brand;
    private List<MaterialDTO> materials;
}
