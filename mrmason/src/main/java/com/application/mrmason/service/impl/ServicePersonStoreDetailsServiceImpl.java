package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ResponseDeleteSPStoreDto;
import com.application.mrmason.dto.ServicePersonStoreResponse;
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;
import com.application.mrmason.repository.ServicePersonStoreDetailsRepo;
import com.application.mrmason.service.ServicePersonStoreDetailsService;

@Service
public class ServicePersonStoreDetailsServiceImpl implements ServicePersonStoreDetailsService {

    private static final Logger log = LoggerFactory.getLogger(ServicePersonStoreDetailsServiceImpl.class);

    @Autowired
    private ServicePersonStoreDetailsRepo spStoreDetailsRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ServicePersonStoreDetailsEntity addStore(ServicePersonStoreDetailsEntity store) {
        log.info("Attempting to add a new store with spUserIdStoreId: {}", store.getSpUserIdStoreId());
        Optional<ServicePersonStoreDetailsEntity> spStoreExists = spStoreDetailsRepo
                .findBySpUserIdStoreId(store.getSpUserIdStoreId());

        if (spStoreExists.isPresent()) {
            log.warn("Store with spUserIdStoreId: {} already exists. Skipping save.", store.getSpUserIdStoreId());
            return null;
        }
        ServicePersonStoreDetailsEntity savedStore = spStoreDetailsRepo.save(store);
        log.info("Store added successfully with spUserIdStoreId: {}", store.getSpUserIdStoreId());
        return savedStore;
    }

    @Override
    public ServicePersonStoreDetailsEntity updateStore(ServicePersonStoreDetailsEntity store) {
        log.info("Attempting to update store with spUserIdStoreId: {}", store.getSpUserIdStoreId());
        Optional<ServicePersonStoreDetailsEntity> idExists = spStoreDetailsRepo.findBySpUserIdStoreId(store.getSpUserIdStoreId());

        if (idExists.isPresent()) {
            ServicePersonStoreDetailsEntity db = idExists.get();
            db.setGst(store.getGst());
            db.setGstDocument(store.getGstDocument());
            db.setLocation(store.getLocation());
            db.setTradeLicense(store.getTradeLicense());
            db.setUpdatedBy(store.getUpdatedBy());
            log.info("Store updated successfully with spUserIdStoreId: {}", store.getSpUserIdStoreId());
            return spStoreDetailsRepo.save(db);
        }
        log.warn("Store with spUserIdStoreId: {} not found. Update failed.", store.getSpUserIdStoreId());
        return null;
    }

    @Override
    public List<ServicePersonStoreDetailsEntity> getSPStoreDetails(String spUserId, String storeId,
            String spUserIdStoreId, String location,
            String gst, String tradeLicense,
            String updatedBy) {
        log.info(
                "Fetching SP Store Details with spUserId: {}, storeId: {}, spUserIdStoreId: {}, location: {}, gst: {}, tradeLicense: {}, updatedBy: {}",
                spUserId, storeId, spUserIdStoreId, location, gst, tradeLicense, updatedBy);

        List<ServicePersonStoreDetailsEntity> details = spStoreDetailsRepo.findByDynamicQuery(spUserId, storeId, spUserIdStoreId,
                location, gst, tradeLicense, updatedBy);
        log.info("Retrieved {} SP Store Details", details.size());
        return details;
    }

    @Override
    public ResponseDeleteSPStoreDto deleteSPStoreDetailsById(String spUserIdStoreId) {
        log.info("Attempting to delete store with spUserIdStoreId: {}", spUserIdStoreId);

        Optional<ServicePersonStoreDetailsEntity> storeDetailsOptional = spStoreDetailsRepo.findBySpUserIdStoreId(spUserIdStoreId);
        if (storeDetailsOptional.isPresent()) {
            ServicePersonStoreDetailsEntity storeDetails = storeDetailsOptional.get();
            spStoreDetailsRepo.deleteById(spUserIdStoreId);
            log.info("Store deleted successfully with spUserIdStoreId: {}", spUserIdStoreId);

            return new ResponseDeleteSPStoreDto("Data deleted successfully for spUserIdStoreId", true,
                    List.of(storeDetails));
        } else {
            log.warn("Store with spUserIdStoreId: {} not found", spUserIdStoreId);
            return new ResponseDeleteSPStoreDto("No data found for the given spUserIdStoreId", false, List.of());
        }
    }

    @Override
    public Optional<ServicePersonStoreDetailsEntity> findStoreById(String spUserIdStoreId) {
        return spStoreDetailsRepo.findBySpUserIdStoreId(spUserIdStoreId);
    }

    @Override
    public Optional<ServicePersonStoreDetailsEntity> findStoreByStoreId(String storeId) {
        return spStoreDetailsRepo.findStoreByStoreId(storeId);
    }

    @Override
    public List<ServicePersonStoreResponse> getDataBy(String location) {
        log.info("Fetching SP Store Details by location: {}", location);
        List<ServicePersonStoreDetailsEntity> spStoreDetails = location != null && !location.isEmpty()
                ? spStoreDetailsRepo.findByLocation(location)
                : List.of();

        List<ServicePersonStoreResponse> response = spStoreDetails.stream()
                .map(spStore -> modelMapper.map(spStore, ServicePersonStoreResponse.class))
                .collect(Collectors.toList());
        log.info("Retrieved {} SP Store Details for location: {}", response.size(), location);
        return response;
    }
}
