package com.application.mrmason.dto;

import java.util.List;
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDeleteSPStoreDto {
    private String message;
    private boolean status;
    private List<ServicePersonStoreDetailsEntity> deletedStoreData;

    public ResponseDeleteSPStoreDto(String message, boolean status, List<ServicePersonStoreDetailsEntity> deletedStoreData) {
        this.message = message;
        this.status = status;
        this.deletedStoreData = deletedStoreData;
    }
}
