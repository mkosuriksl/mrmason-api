package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface PromotionService {
	public String sendOtpToAllUsers(RegSource regSource);
}
