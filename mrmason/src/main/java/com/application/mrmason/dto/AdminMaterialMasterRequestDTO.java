package com.application.mrmason.dto;

import com.application.mrmason.entity.AdminMaterialMaster;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminMaterialMasterRequestDTO {
    private List<AdminMaterialMaster> materials;
}

