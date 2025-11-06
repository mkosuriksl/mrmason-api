package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.FrAvaiableLocation;
import com.application.mrmason.entity.FrAvailable;
import com.application.mrmason.entity.FrPositionType;
import com.application.mrmason.entity.FrProfile;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.entity.FrServiceRole;

import lombok.Data;

@Data
public class ResponseGetdetailsDto {
    private String message;
    private boolean status;

    private List<FrAvaiableLocation> frAvaiableLocation;
    private List<FrAvailable> frAvaiable;
    private List<FrPositionType> frPositionType;
    private List<FrProfile> frProfile;
    private List<FrReg> frReg;
    private List<FrServiceRole> frServiceRole;

    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
