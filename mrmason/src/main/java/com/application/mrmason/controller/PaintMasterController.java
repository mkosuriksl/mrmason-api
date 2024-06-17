package com.application.mrmason.controller;

import com.application.mrmason.dto.PaintMasterRequestDto;
import com.application.mrmason.dto.PaintMasterResponseDto;
import com.application.mrmason.service.PaintMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paint-master")
public class PaintMasterController {

    @Autowired
    private PaintMasterService paintMasterService;

    @PostMapping("/add")
    public ResponseEntity<PaintMasterResponseDto> addPaintMaster(@RequestBody PaintMasterRequestDto paintMasterRequestDto) {
        PaintMasterResponseDto responseDto = paintMasterService.addPaintMaster(paintMasterRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/{colorCode}")
    public ResponseEntity<PaintMasterResponseDto> updatePaintMaster(@PathVariable int colorCode, @RequestBody PaintMasterRequestDto paintMasterRequestDto) {
        PaintMasterResponseDto responseDto = paintMasterService.updatePaintMaster(colorCode, paintMasterRequestDto);
        return ResponseEntity.ok(responseDto);
    }





}
