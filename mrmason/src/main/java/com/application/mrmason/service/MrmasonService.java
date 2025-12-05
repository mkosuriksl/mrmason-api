package com.application.mrmason.service;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.application.mrmason.dto.PaginatedResponse;
import com.application.mrmason.dto.ServiceRequestHeaderDTO;
import com.application.mrmason.entity.CarstandUrlAndApiKeyEntity;
import com.application.mrmason.enums.Status;
import com.application.mrmason.repository.CarstandUrlAndApiKeyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class MrmasonService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	CarstandUrlAndApiKeyRepository apkKeyRepository;

	public PaginatedResponse<ServiceRequestHeaderDTO> searchServiceRequests(
	        String fromRequestDate,
	        String toRequestDate,
	        String requestId,
	        String serviceId,
	        Status status,
	        String contactNumber, // Fixed typo
	        String brand,
	        String vehicleId,
	        String model,
	        int pageNo,
	        int pageSize,
	        Map<String, String> requestParams) {

	    // Fetch API URL + Key
	    CarstandUrlAndApiKeyEntity smsDetails = apkKeyRepository.findById(1L)
	            .orElseThrow(() -> new RuntimeException("API Key not found"));

	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(smsDetails.getUrl());

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("token", smsDetails.getApiKey());

	    // Add query parameters dynamically
	    if (fromRequestDate != null && !fromRequestDate.isEmpty())
	        builder.queryParam("fromRequestDate", fromRequestDate);

	    if (toRequestDate != null && !toRequestDate.isEmpty())
	        builder.queryParam("toRequestDate", toRequestDate);

	    if (requestId != null && !requestId.isEmpty())
	        builder.queryParam("requestId", requestId);

	    if (serviceId != null && !serviceId.isEmpty())
	        builder.queryParam("serviceId", serviceId);

	    if (status != null)
	        builder.queryParam("status", status.name());

	    if (contactNumber != null && !contactNumber.isEmpty())
	        builder.queryParam("contactNumber", contactNumber);

	    if (brand != null && !brand.isEmpty())
	        builder.queryParam("brand", brand);

	    if (vehicleId != null && !vehicleId.isEmpty())
	        builder.queryParam("vehicleId", vehicleId);

	    if (model != null && !model.isEmpty())
	        builder.queryParam("model", model);

	    builder.queryParam("pageNo", pageNo);
	    builder.queryParam("pageSize", pageSize);

	    HttpEntity<?> entity = new HttpEntity<>(headers);

	    ResponseEntity<String> response = restTemplate.exchange(
	            builder.toUriString(),
	            HttpMethod.GET,
	            entity,
	            String.class
	    );

	    // Convert JSON to PaginatedResponse<ServiceRequestHeaderDTO>
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        // Register module to handle Java 8 LocalDate properly
	        mapper.registerModule(new JavaTimeModule());
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	        return mapper.readValue(
	                response.getBody(),
	                new TypeReference<PaginatedResponse<ServiceRequestHeaderDTO>>() {}
	        );

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to parse response", e);
	    }
	}


}
