package com.application.mrmason.dto;
import java.util.List;

import com.application.mrmason.entity.AdminMaterialMaster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMaterialMasterResponseDTO {
    private List<AdminMaterialMasterResponseWithImageDto> materials;
    private List<AdminDetailsDto> admins;
    private List<MaterialSupplierDto> suppliers;
}


