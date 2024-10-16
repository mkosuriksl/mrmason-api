package com.application.mrmason.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminSms;
import com.application.mrmason.repository.AdminSmsRepo;
import com.application.mrmason.service.SmsSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService implements SmsSender {

	@Autowired
	private AdminSmsRepo smsRepo;

	@Override
	public boolean sendSMSMessage(String phoneNumber, String message) {
		log.info("sendSMSMessage Service Called=======>: ({}, {})", phoneNumber,message);
		try {
			Optional<AdminSms> sms = smsRepo.findByActive();
			if (sms.isEmpty()) {
				log.info("There is not any active Sms Creds =*=*=*=*=*=*=>: ({}, {})", phoneNumber, message);
			}
			// Construct message content with OTP

			// Encode message content and other parameters
			String key=  new String(Base64.getDecoder().decode(sms.get().getApiKey()));
			String apiKey = URLEncoder.encode(key, StandardCharsets.UTF_8.toString());
			String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
			String sender = URLEncoder.encode(sms.get().getSender(), StandardCharsets.UTF_8.toString());
			String numbers = URLEncoder.encode(phoneNumber, StandardCharsets.UTF_8.toString());

			// Construct URL
			String url = sms.get().getUrl() + "apikey=" + apiKey + "&numbers=" + numbers + "&message="
					+ encodedMessage + "&sender=" + sender;

			// Create HTTP connection
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			// Read response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder smsResponse = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				smsResponse.append(line).append(" ");
			}
			rd.close();

			// Log response
			log.info("Response From SMS Service: {}", smsResponse);
			return true;
		} catch (Exception e) {
			log.error("Exception while Sending SMS to Mobile Number {} with error: {}", phoneNumber, e.getMessage());
			return false;
		}
	}

}
