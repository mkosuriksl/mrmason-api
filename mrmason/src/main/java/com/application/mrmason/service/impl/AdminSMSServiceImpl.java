package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.ResponseAdminMailDto;
import com.application.mrmason.entity.AdminSms;
import com.application.mrmason.repository.AdminSmsRepo;
import com.application.mrmason.service.AdminSMSService;

@Service
public class AdminSMSServiceImpl implements AdminSMSService {

	@Autowired
	private AdminSmsRepo smsRepo;

	ResponseAdminMailDto response = new ResponseAdminMailDto();

	@Override
	@Transactional
	public ResponseAdminMailDto addApiRequest(AdminSms admin) {
		List<AdminSms> smsDetails = smsRepo.findAllByActive();
		if (admin.isActive() == true) {
			if (!smsDetails.isEmpty()) {
				smsDetails.stream().forEach(sms -> {
					sms.setActive(false);
				});
			}
		}

		String encodedApiKey = Base64.getEncoder().encodeToString(admin.getApiKey().getBytes());
		admin.setApiKey(encodedApiKey);
		admin.setCreatedDate(LocalDateTime.now());
		admin.setUpdatedDate(LocalDateTime.now());
		AdminSms data = smsRepo.save(admin);
		response.setMessage("Admin mail added successfully.");
		response.setStatus(true);
		response.setData(data);
		return response;
	}

	@Override
	public List<AdminSms> getAllSMSDetails() {
		return smsRepo.findAll();
	}

	@Override
	@Transactional
	public ResponseAdminMailDto updateApiRequest(AdminSms admin) {
		Optional<AdminSms> data = smsRepo.findById(admin.getId());
		if (data.isPresent()) {
			String encodedApiKey = Base64.getEncoder().encodeToString(admin.getApiKey().getBytes());
			data.get().setApiKey(encodedApiKey);
			data.get().setUpdatedBy(admin.getUpdatedBy());
			data.get().setUpdatedDate(LocalDateTime.now());
			AdminSms updatedData = smsRepo.save(data.get());
			response.setMessage("Admin mail details updated successfully.");
			response.setStatus(true);
			response.setData(updatedData);
			return response;
		}
		response.setMessage("No record found for this requested ID..!");
		response.setStatus(false);
		return response;

	}

}
