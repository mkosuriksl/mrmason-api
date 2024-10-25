package com.application.mrmason.controller;

import com.application.mrmason.dto.AdminUiEndPointDto;
import com.application.mrmason.dto.ResponseAdminUiEndPointDto;
import com.application.mrmason.entity.AdminUiEndPointEntity;
import com.application.mrmason.service.AdminUiEndPointService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@PreAuthorize("hasAuthority('Adm')")
public class AdminUiEndPointController {

    @Autowired
    private AdminUiEndPointService service;

    @PostMapping("/addAdminUiEndPoint")
    public ResponseEntity<ResponseAdminUiEndPointDto<AdminUiEndPointEntity>> saveOrUpdate(
            @RequestBody AdminUiEndPointDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.save(dto));
    }

    @PutMapping("/updateAdminUiEndPoint")
    public ResponseEntity<ResponseAdminUiEndPointDto<AdminUiEndPointEntity>> update(
            @RequestBody AdminUiEndPointDto dto,
            @RequestParam(required = false) String oldIpUrlToUi) {

        return ResponseEntity.ok(service.update(dto, dto.getOldIpUrlToUi()));
    }

    @GetMapping("/getAdminUiEndPoint")
    public ResponseEntity<ResponseAdminUiEndPointDto<List<AdminUiEndPointEntity>>> getById(
            @RequestParam(required = false) String systemId,
            @RequestParam(required = false) String ipUrlToUi) {
        ResponseAdminUiEndPointDto<List<AdminUiEndPointEntity>> response = service.getById(systemId, ipUrlToUi);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllAdminUiEndPoint")
    public ResponseEntity<ResponseAdminUiEndPointDto<List<AdminUiEndPointEntity>>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
