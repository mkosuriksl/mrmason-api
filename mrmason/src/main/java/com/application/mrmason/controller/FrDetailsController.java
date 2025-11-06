package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseGetdetailsDto;
import com.application.mrmason.service.FrDetailsService;

@RestController
@RequestMapping("/freelancer")
public class FrDetailsController {

    @Autowired
    private FrDetailsService frDetailsService;

    @GetMapping("/getDetails")
    public ResponseEntity<ResponseGetdetailsDto> getFreelancerDetails(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String primarySkill,
            @RequestParam(required = false) String frEmail,
            @RequestParam(required = false) String secondarySkill,
            @RequestParam(required = false) String positionType,
            @RequestParam(required = false) String training,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ResponseGetdetailsDto response = frDetailsService.getFreelancerDetails(
                city, primarySkill, frEmail, secondarySkill, positionType, training, page, size);
        return ResponseEntity.ok(response);
    }
}

