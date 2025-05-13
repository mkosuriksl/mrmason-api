package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SiteMeasurementService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
    
//    @Override
//	public List<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location) {
//
//	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//	    CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
//	    Root<SiteMeasurement> root = query.from(SiteMeasurement.class);
//
//	    List<Predicate> predicates = new ArrayList<>();
//
//	    if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
//	    }
//	    if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("eastSiteLegth"), eastSiteLegth));
//	    }
//	    if (location != null && !location.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("location"), location));
//	    }
//
//	    query.select(root);
//	    if (!predicates.isEmpty()) {
//	        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
//	    }
//
//	    return entityManager.createQuery(query).getResultList();
//    }
    @Override
    public Page<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location, Pageable pageable) {
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
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<SiteMeasurement> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // For total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<SiteMeasurement> countRoot = countQuery.from(SiteMeasurement.class);

        List<Predicate> countPredicates = new ArrayList<>();
        if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
            countPredicates.add(cb.equal(countRoot.get("serviceRequestId"), serviceRequestId));
        }
        if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
            countPredicates.add(cb.equal(countRoot.get("eastSiteLegth"), eastSiteLegth));
        }
        if (location != null && !location.trim().isEmpty()) {
            countPredicates.add(cb.equal(countRoot.get("location"), location));
        }

        countQuery.select(cb.count(countRoot));
        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }


    @Override
    public SiteMeasurement findByServiceRequestId(String serviceRequestId) {
        return repository.findByServiceRequestId(serviceRequestId);
    }
}