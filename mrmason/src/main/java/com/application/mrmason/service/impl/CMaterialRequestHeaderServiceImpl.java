package com.application.mrmason.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.repository.CMaterialRequestHeaderRepository;
import com.application.mrmason.service.CMaterialRequestHeaderService;

import java.util.List;
import java.util.Optional;

@Service
public class CMaterialRequestHeaderServiceImpl implements CMaterialRequestHeaderService {

    @Autowired
    private CMaterialRequestHeaderRepository repository;

    @Override
    public CMaterialRequestHeaderEntity addMaterialRequest(CMaterialRequestHeaderEntity request) {
        return repository.save(request);
    }

    @Override
    public CMaterialRequestHeaderEntity updateMaterialRequest(String id, CMaterialRequestHeaderEntity request) {
        Optional<CMaterialRequestHeaderEntity> existingRequest = repository.findById(id);
        if (existingRequest.isPresent()) {
            CMaterialRequestHeaderEntity updatedRequest = existingRequest.get();
            updatedRequest.setCreatedDate(request.getCreatedDate());
            updatedRequest.setUpdatedBy(request.getUpdatedBy());
            updatedRequest.setUpdatedDate(request.getUpdatedDate());
            updatedRequest.setQuoteId(request.getQuoteId());
            updatedRequest.setCustomerId(request.getCustomerId());
            updatedRequest.setCustomerMobile(request.getCustomerMobile());
            updatedRequest.setDeliveryDate(request.getDeliveryDate());
            updatedRequest.setDeliveryLocation(request.getDeliveryLocation());
            updatedRequest.setCustomerEmail(request.getCustomerEmail());
            updatedRequest.setCustomerName(request.getCustomerName());
            return repository.save(updatedRequest);
        } else {
            throw new RuntimeException("Material Request not found with ID: " + id);
        }
    }

    @Override
    public List<CMaterialRequestHeaderEntity> getAllMaterialRequests() {
        return repository.findAll();
    }
}