package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.ResponseAdminApiUrlDto;
import com.application.mrmason.entity.AdminApiUrl;

public interface AdminApiUrlService {
	ResponseAdminApiUrlDto addApiRequest(AdminApiUrl api);
//	ResponseGetApiUrlDto getApiRequest(String systemId,String updatedBy,String ip);
	ResponseAdminApiUrlDto updateApiRequest(AdminApiUrl api);
	public Page<AdminApiUrl> getApiRequest(String systemId, String updatedBy, String ip, Pageable pageable) ;
}
