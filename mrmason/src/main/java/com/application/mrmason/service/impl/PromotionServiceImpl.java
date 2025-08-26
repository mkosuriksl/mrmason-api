package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.User;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.EmailService;
import com.application.mrmason.service.PromotionService;
import com.application.mrmason.service.SmsSender;

@Service
public class PromotionServiceImpl implements PromotionService{

	@Autowired
	UserDAO userDAO;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsSender smsService;
    
	@Override
	public String sendOtpToAllUsers(RegSource regSource) {
		List<User> users = userDAO.findAll();

        if (users.isEmpty()) {
            return "No users found!";
        }

        for (User user : users) {

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendEmail(user.getEmail(), regSource);
            }

            if (user.getMobile() != null && !user.getMobile().isEmpty()) {
                smsService.sendSMSMessage(user.getMobile(), regSource);
            }
        }

        return "OTP sent successfully to all users.";
    }

}
