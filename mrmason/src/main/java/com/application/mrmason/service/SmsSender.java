package com.application.mrmason.service;

import com.application.mrmason.enums.RegSource;

public interface SmsSender {

	boolean sendSMSMessage(String phoneNumber, String message, RegSource regSource);

	boolean registrationSendSMSMessage(String phoneNumber, String message, RegSource regSource);

}
