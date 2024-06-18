package com.application.mrmason.dto;

import lombok.Data;

import java.util.Date;
@Data
public class PaintMasterResponseDto {
    private int colorCode;
    private String colorImage;
    private String wallType;
    private Date updatedDate;
    private String brand;

}
