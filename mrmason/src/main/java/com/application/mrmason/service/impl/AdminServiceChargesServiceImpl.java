package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminServiceCharges;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.AdminServiceChargesRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.AdminServiceChargesService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminServiceChargesServiceImpl implements AdminServiceChargesService {

	@Autowired
	AdminServiceChargesRepo repo;

	@PersistenceContext
	private EntityManager entityManager;

//	@Autowired
//	public AdminDetailsRepo adminRepo;
	
	@Autowired
	UserDAO userDAO;
//	@Override
//    public List<AdminServiceCharges> addCharges(List<AdminServiceCharges> chargesList) {
//        List<AdminServiceCharges> savedCharges = new ArrayList<>();
//        
//       
//        for (AdminServiceCharges charges : chargesList) {
//            charges.setServiceChargeKey(generateServiceChargeKey(charges.getServiceId(), charges.getLocation(), charges.getBrand(), charges.getModel()));
//            
//            Optional<AdminDetails> user = Optional
//    				.ofNullable(adminRepo.findByEmail(charges.getEmail()));
//    	if(user.isPresent()) {
//    		 charges.setUpdatedBy(charges.getEmail());
//            Optional<AdminServiceCharges> existingCharges = repo.findById(charges.getServiceChargeKey());
//            if (!existingCharges.isPresent()) {
//                repo.save(charges);
//                repo.flush();  // Flush the changes to the database
////                savedCharges.add(charges);
//                savedCharges.add(charges);
//            }
//        }
//       }	
//        return savedCharges;
//    }
//	
//
//	private String generateServiceChargeKey(String serviceId, String location, String brand, String model) {
//        String subString = location.substring(0, Math.min(4, location.length()));
//        return serviceId + "_" + brand + "_" + model + "_" + subString;
//    }

	
	
//	@Transactional
	@Override
    public List<AdminServiceCharges> addCharges(List<AdminServiceCharges> chargesList) {
        List<AdminServiceCharges> savedCharges = new ArrayList<>();

        for (AdminServiceCharges charges : chargesList) {
            charges.setServiceChargeKey(generateServiceChargeKey(charges.getServiceId(), charges.getLocation(), charges.getBrand(), charges.getModel()));
            Optional<User> user = userDAO.findById(charges.getBodSeqNo());

            if (user.isPresent()) {
                charges.setUpdatedBy(charges.getBodSeqNo());
                Optional<AdminServiceCharges> existingCharges = repo.findById(charges.getServiceChargeKey());

                if (!existingCharges.isPresent()) {
                    repo.save(charges);
                    repo.flush();  // Flush the changes to the database
                    savedCharges.add(charges);
                }
            }
        }
        return savedCharges;
    }

    private String generateServiceChargeKey(String serviceId, String location, String brand, String model) {
        String subString = location.substring(0, Math.min(4, location.length()));
        return serviceId + "_" + brand + "_" + model + "_" + subString;
    }

	
	@Override
	public Page<AdminServiceCharges> getAdminServiceCharges(String serviceChargeKey, String serviceId, String location,
			String brand, String model,String updatedBy,String subcategory,Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminServiceCharges> query = cb.createQuery(AdminServiceCharges.class);
		Root<AdminServiceCharges> root = query.from(AdminServiceCharges.class);
		List<Predicate> predicates = new ArrayList<>();

		if (serviceChargeKey != null) {
			predicates.add(cb.equal(root.get("serviceChargeKey"), serviceChargeKey));
		}
		if (serviceId != null) {
			predicates.add(cb.equal(root.get("serviceId"), serviceId));
		}
		if (location != null) {
			predicates.add(cb.equal(root.get("location"), location));
		}
		if (brand != null) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (model != null) {
			predicates.add(cb.equal(root.get("model"), model));
		}
		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
		if (subcategory != null) {
			predicates.add(cb.equal(root.get("subcategory"), subcategory));
		}
		query.where(predicates.toArray(new Predicate[0]));

		query.where(predicates.toArray(new Predicate[0]));
	    TypedQuery<AdminServiceCharges> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // Count query
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<AdminServiceCharges> countRoot = countQuery.from(AdminServiceCharges.class);
	    countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Override
	public AdminServiceCharges updateCharges(AdminServiceCharges charges) {

		Optional<AdminServiceCharges> serviceChargeKeyExists = repo.findById(charges.getServiceChargeKey());
		if (serviceChargeKeyExists.isPresent()) {
			serviceChargeKeyExists.get().setServiceCharge(charges.getServiceCharge());
			return repo.save(serviceChargeKeyExists.get());
		} else {
			return null;
		}

	}

}
