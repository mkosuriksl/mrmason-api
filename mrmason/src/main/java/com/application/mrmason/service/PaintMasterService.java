package com.application.mrmason.service;

import com.application.mrmason.dto.PaintMasterRequestDto;
import com.application.mrmason.dto.PaintMasterResponseDto;

import java.util.List;

public interface PaintMasterService {
    PaintMasterResponseDto addPaintMaster(PaintMasterRequestDto paintMasterRequestDto);
    PaintMasterResponseDto updatePaintMaster(int colorCode, PaintMasterRequestDto paintMasterRequestDto);
  //  List<PaintMasterResponseDto> getPaintMasterByColorCode(int colorCode, String brand);


}
