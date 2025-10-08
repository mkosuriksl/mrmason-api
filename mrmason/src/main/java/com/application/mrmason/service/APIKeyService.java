package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.APIKEY;

public interface APIKeyService {
	public APIKEY addApiKey(APIKEY apiKey);
	public APIKEY updateApiKey(APIKEY updatedApiKey);
	public Page<APIKEY> get(String apiKey, String updatedBy,Pageable pageable) throws AccessDeniedException;
}
