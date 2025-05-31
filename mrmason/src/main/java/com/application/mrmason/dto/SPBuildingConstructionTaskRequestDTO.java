package com.application.mrmason.dto;

import com.application.mrmason.entity.SPBuildingConstructionTasksManagment;
import lombok.Data;
import java.util.List;

@Data
public class SPBuildingConstructionTaskRequestDTO {
    private String userId;
    private List<SPBuildingConstructionTasksManagment> tasks;
}
