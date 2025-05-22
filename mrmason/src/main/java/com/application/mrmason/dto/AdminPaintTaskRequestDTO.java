package com.application.mrmason.dto;

import com.application.mrmason.entity.AdminPaintTasksManagemnt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPaintTaskRequestDTO {
    private String userId;
    private List<AdminPaintTasksManagemnt> tasks;
}

