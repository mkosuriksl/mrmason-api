package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.PromotionService;

@RestController
public class PromotionController {
	@Autowired
    private PromotionService promotionService;

    @PostMapping("/api/promotions/send")
    public String sendOtpToAll(@RequestParam RegSource regSource) {
        return promotionService.sendOtpToAllUsers(regSource);
    }
}
