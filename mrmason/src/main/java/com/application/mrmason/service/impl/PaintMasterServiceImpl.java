package com.application.mrmason.service.impl;

import com.application.mrmason.dto.PaintMasterRequestDto;
import com.application.mrmason.dto.PaintMasterResponseDto;
import com.application.mrmason.entity.PaintMaster;
import com.application.mrmason.repository.PaintMasterRepo;
import com.application.mrmason.service.PaintMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaintMasterServiceImpl implements PaintMasterService {

    @Autowired
    private PaintMasterRepo paintMasterRepo;


    @Override
    public PaintMasterResponseDto addPaintMaster(PaintMasterRequestDto paintMasterRequestDto) {
        Optional<PaintMaster> existingPaintMaster = paintMasterRepo.findById(paintMasterRequestDto.getColorCode());
        if (existingPaintMaster.isPresent()) {
            throw new RuntimeException("PaintMaster with color code " + paintMasterRequestDto.getColorCode() + " already exists");
        }
        // If not present, create a new PaintMaster entity
        PaintMaster paintMaster = new PaintMaster();
        paintMaster.setColorCode(paintMasterRequestDto.getColorCode());
        paintMaster.setColorImage(paintMasterRequestDto.getColorImage());
        paintMaster.setWallType(paintMasterRequestDto.getWallType());
        paintMaster.setBrand(paintMasterRequestDto.getBrand());

        // Save the new PaintMaster entity
        PaintMaster savedPaintMaster = paintMasterRepo.save(paintMaster);

        // Convert to DTO and return
        return toResponseDto(savedPaintMaster);
    }

    @Override
    public PaintMasterResponseDto updatePaintMaster(int colorCode, PaintMasterRequestDto paintMasterRequestDto) {
            PaintMaster paintMaster = paintMasterRepo.findById(colorCode)
                    .orElseThrow(() -> new RuntimeException("PaintMaster not found"));
            paintMaster.setColorImage(paintMasterRequestDto.getColorImage());
            paintMaster.setWallType(paintMasterRequestDto.getWallType());
            paintMaster.setBrand(paintMasterRequestDto.getBrand());
            PaintMaster updatedPaintMaster = paintMasterRepo.save(paintMaster);

            return toResponseDto(updatedPaintMaster);
    }


//    @Override
//    public List<PaintMasterResponseDto> getPaintMasterByColorCode(int colorCode, String brand) {
//        List<PaintMaster> paintMaster=paintMasterRepo.findByIdAndBrand(colorCode, brand);
//        return paintMaster.stream().map(this::toResponseDto).collect(Collectors.toList());
//    }


    private PaintMasterResponseDto toResponseDto(PaintMaster paintMaster) {
        PaintMasterResponseDto responseDto = new PaintMasterResponseDto();
        responseDto.setColorCode(paintMaster.getColorCode());
        responseDto.setColorImage(paintMaster.getColorImage());
        responseDto.setWallType(paintMaster.getWallType());
        responseDto.setUpdatedDate(paintMaster.getUpdatedDate());
        responseDto.setBrand(paintMaster.getBrand());
        return responseDto;
    }
}
