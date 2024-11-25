package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.service.CMaterialRequestHeaderService;

import java.util.List;

@RestController
public class CMaterialRequestHeaderController {

    @Autowired
    private CMaterialRequestHeaderService service;

    @PostMapping("/add-material-header")
    public ResponseEntity<CMaterialRequestHeaderEntity> createMaterialRequest(
            @RequestBody CMaterialRequestHeaderEntity request) {
        CMaterialRequestHeaderEntity createdRequest = service.addMaterialRequest(request);
        return ResponseEntity.ok(createdRequest);
    }

    @PutMapping("/update-material-header")
    public ResponseEntity<CMaterialRequestHeaderEntity> updateMaterialRequest(
            @PathVariable String id, @RequestBody CMaterialRequestHeaderEntity request) {
        CMaterialRequestHeaderEntity updatedRequest = service.updateMaterialRequest(id, request);
        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/get-material-header")
    public ResponseEntity<List<CMaterialRequestHeaderEntity>> getAllMaterialRequests() {
        List<CMaterialRequestHeaderEntity> requests = service.getAllMaterialRequests();
        return ResponseEntity.ok(requests);
    }
}