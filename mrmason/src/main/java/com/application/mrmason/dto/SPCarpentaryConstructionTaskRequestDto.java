package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.SPCarpentaryConstructionTasksManagement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SPCarpentaryConstructionTaskRequestDto {
    private String userId;
    private List<SPCarpentaryConstructionTasksManagement> tasks;
}
