package com.application.mrmason.dto;

import lombok.Data;

@Data
public class PaintMasterRequestDto {

    private int colorCode;
    private String colorImage;
    private String wallType;
    private String brand;

}
