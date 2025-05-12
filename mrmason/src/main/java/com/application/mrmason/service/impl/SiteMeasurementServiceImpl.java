package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.CustomerLoginRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SiteMeasurementService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SiteMeasurementServiceImpl implements SiteMeasurementService {

    @Autowired
    private SiteMeasurementRepository repository;
    
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	CustomerRegistrationRepo repo;
	
    @Override
    public SiteMeasurement addSiteMeasurement(SiteMeasurement measurement) {
    	String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
    	CustomerRegistration login = repo.findByUserEmail(loggedInUserEmail);
    	measurement.setUpdatedBy(login.getUserid());  
    	measurement.setUpdatedDate(new Date());       

        return repository.save(measurement);
    }

    @Override
    public SiteMeasurement updateSiteMeasurement(SiteMeasurement measurement) {
        SiteMeasurement existingMeasurement = repository.findByServiceRequestId(measurement.getServiceRequestId());
        if (existingMeasurement != null) {
            existingMeasurement.setEastSiteLegth(measurement.getEastSiteLegth());
            existingMeasurement.setWestSiteLegth(measurement.getWestSiteLegth());
            existingMeasurement.setSouthSiteLegth(measurement.getSouthSiteLegth());
            existingMeasurement.setNorthSiteLegth(measurement.getNorthSiteLegth());
            existingMeasurement.setLocation(measurement.getLocation());
            existingMeasurement.setExpectedBedRooms(measurement.getExpectedBedRooms());
            existingMeasurement.setExpectedAttachedBathRooms(measurement.getExpectedAttachedBathRooms());
            existingMeasurement.setExpectedAdditionalBathRooms(measurement.getExpectedAdditionalBathRooms());
            existingMeasurement.setExpectedStartDate(measurement.getExpectedStartDate());
            return repository.save(existingMeasurement);
        }
        return null;
    }
    
    @Override
	public List<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
	    Root<SiteMeasurement> root = query.from(SiteMeasurement.class);

	    List<Predicate> predicates = new ArrayList<>();

	    if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
	    }
	    if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("eastSiteLegth"), eastSiteLegth));
	    }
	    if (location != null && !location.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("location"), location));
	    }

	    query.select(root);
	    if (!predicates.isEmpty()) {
	        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
	    }

	    return entityManager.createQuery(query).getResultList();
    }

    @Override
    public SiteMeasurement findByServiceRequestId(String serviceRequestId) {
        return repository.findByServiceRequestId(serviceRequestId);
    }
}