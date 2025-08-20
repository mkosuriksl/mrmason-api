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

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.ResponseGetGstInServiceUserDto;
import com.application.mrmason.entity.GstInServiceUser;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.GstInServiceUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gst")
@RequiredArgsConstructor
public class GstServiceUserController {

	@Autowired
    private GstInServiceUserService gstInServiceUserService ;

    @PostMapping("/add")
    public ResponseEntity<GenericResponse<List<GstInServiceUser>>> saveGst(
            @RequestBody List<GstInServiceUser> quotations,
            @RequestParam RegSource regSource) {

        List<GstInServiceUser> response = gstInServiceUserService.saveGst(quotations, regSource);

        GenericResponse<List<GstInServiceUser>> genericResponse =
                new GenericResponse<>("Gst saved successfully", true, response);

        return ResponseEntity.ok(genericResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<GenericResponse<List<GstInServiceUser>>> updateTasks(
             @RequestBody List<GstInServiceUser> requestList,@RequestParam RegSource regSource) throws AccessDeniedException {
        List<GstInServiceUser> response = gstInServiceUserService.updateGst(requestList, regSource);
        GenericResponse<List<GstInServiceUser>>genericResponse=new GenericResponse<>("Updated Gst is successfully",true,response);
        return ResponseEntity.ok(genericResponse);
    }

    @GetMapping("/get")
	public ResponseEntity<ResponseGetGstInServiceUserDto> getGst(
			@RequestParam(required = false) String bodSeqNo, @RequestParam(required = false) String gst,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) RegSource regSource, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<GstInServiceUser> srpqPage = gstInServiceUserService.getGst(bodSeqNo, gst, userId,
				regSource, pageable);
		ResponseGetGstInServiceUserDto response = new ResponseGetGstInServiceUserDto();

		response.setMessage("Gst details retrieved successfully.");
		response.setStatus(true);
		response.setGstInServiceUsers(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
