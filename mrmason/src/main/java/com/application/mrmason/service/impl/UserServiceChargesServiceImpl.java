package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserServiceCharges;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.repository.UserServiceChargesRepo;
import com.application.mrmason.service.UserServiceChargesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class UserServiceChargesServiceImpl implements UserServiceChargesService{
	
	@Autowired
	UserServiceChargesRepo repo;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	UserDAO userDAO;
	
	@Override
    public List<UserServiceCharges> addCharges(List<UserServiceCharges> chargesList) {
        List<UserServiceCharges> savedCharges = new ArrayList<>();

        for (UserServiceCharges charges : chargesList) {
            charges.setServiceChargeKey(generateServiceChargeKey(charges.getServiceId(), charges.getLocation(), charges.getBrand(), charges.getModel()));
            Optional<User> user = userDAO.findById(charges.getBodSeqNo());

            if (user.isPresent()) {
                charges.setUpdatedBy(charges.getBodSeqNo());
                Optional<UserServiceCharges> existingCharges = repo.findById(charges.getServiceChargeKey());

                if (!existingCharges.isPresent()) {
                    repo.save(charges);
                    repo.flush();  
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
	public List<UserServiceCharges> getUserServiceCharges(String serviceChargeKey, String serviceId, String location,
			String brand, String model,String updatedBy,String subcategory) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserServiceCharges> query = cb.createQuery(UserServiceCharges.class);
		Root<UserServiceCharges> root = query.from(UserServiceCharges.class);
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

		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public UserServiceCharges updateCharges(UserServiceCharges charges) {

		Optional<UserServiceCharges> serviceChargeKeyExists = repo.findById(charges.getServiceChargeKey());
		if (serviceChargeKeyExists.isPresent()) {
			serviceChargeKeyExists.get().setServiceCharge(charges.getServiceCharge());
			return repo.save(serviceChargeKeyExists.get());
		} else {
			return null;
		}

	}

}
