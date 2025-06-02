package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.AdminMaterialMasterRequestDTO;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetAdminMaterialMasterDto;
import com.application.mrmason.entity.AdminMaterialMaster;
import com.application.mrmason.service.AdminMaterialMasterService;



@RestController
@RequestMapping("/admin-material-master")
public class AdminMaterialMasterController {

    @Autowired
    private AdminMaterialMasterService adminMaterialMasterService;
    
    @PostMapping("/add")
    public ResponseEntity<GenericResponse<List<AdminMaterialMaster>>> createAdminMaterialMaster(
            @RequestBody AdminMaterialMasterRequestDTO requestDTO)throws AccessDeniedException  {
        List<AdminMaterialMaster> savedMaterials =
                adminMaterialMasterService.createAdminMaterialMaster(requestDTO.getMaterials());
        GenericResponse<List<AdminMaterialMaster>> response = new GenericResponse<>("Admin Material Master Saved Successfully",
				true, savedMaterials);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update")
    public ResponseEntity<GenericResponse<List<AdminMaterialMaster>>> updateAdminMaterialMasters(
            @RequestBody AdminMaterialMasterRequestDTO requestDTO) throws AccessDeniedException {

        List<AdminMaterialMaster> updatedMaterials =
                adminMaterialMasterService.updateAdminMaterialMasters(requestDTO.getMaterials());
        GenericResponse<List<AdminMaterialMaster>> response = new GenericResponse<>("Admin Material Master Saved Successfully",
				true, updatedMaterials);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get")
	public ResponseEntity<ResponseGetAdminMaterialMasterDto> getServiceRequestPaintQuotationService(
			@RequestParam(required = false) String materialCategory, @RequestParam(required = false) String materialSubCategory,
			@RequestParam(required = false) String brand, @RequestParam(required = false) String modelNo,
			@RequestParam(required = false) String brandsize, @RequestParam(required = false) String shape,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<AdminMaterialMaster> srpqPage = adminMaterialMasterService.getAdminMaterialMaster(materialCategory,materialSubCategory,
				brand, modelNo, brandsize,shape, pageable);
		ResponseGetAdminMaterialMasterDto response = new ResponseGetAdminMaterialMasterDto();

		response.setMessage("Admin Material Master details retrieved successfully.");
		response.setStatus(true);
		response.setGetAdminMaterialMaster(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

