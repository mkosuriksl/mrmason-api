package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.UserChargeChildDTO;
import com.application.mrmason.dto.UserChargeResponseDTO;
import com.application.mrmason.dto.UserServiceChargeRequest;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserServiceCharges;
import com.application.mrmason.exceptions.ResourceNotFoundException;
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
public class UserServiceChargesServiceImpl implements UserServiceChargesService {

	@Autowired
	UserServiceChargesRepo repo;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	UserDAO userDAO;

	@Override
	public List<UserServiceCharges> addCharges(UserServiceChargeRequest serviceChargeRequest) {
		List<UserServiceCharges> savedCharges = new ArrayList<>();

		for (UserServiceCharges charges : serviceChargeRequest.getChargesList()) {
			charges.setServiceChargeKey(generateServiceChargeKey(charges.getServiceId(), charges.getLocation(),
					charges.getBrand(), charges.getModel()));
			charges.setSubcategory(serviceChargeRequest.getSubCategory());
			charges.setUpdatedDate(LocalDateTime.now().toString());
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
			String brand, String model, String userId, String subcategory) {
		List<UserServiceCharges> root = repo.findbySearchValue(serviceChargeKey, serviceId, location, brand, model,
				userId, subcategory);
		/*
		 * CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		 * CriteriaQuery<UserServiceCharges> query =
		 * cb.createQuery(UserServiceCharges.class); Root<UserServiceCharges> root =
		 * query.from(UserServiceCharges.class); List<Predicate> predicates = new
		 * ArrayList<>();
		 * 
		 * if (serviceChargeKey != null) {
		 * predicates.add(cb.equal(root.get("serviceChargeKey"), serviceChargeKey)); }
		 * if (serviceId != null) { predicates.add(cb.equal(root.get("serviceId"),
		 * serviceId)); } if (location != null) {
		 * predicates.add(cb.equal(root.get("location"), location)); } if (brand !=
		 * null) { predicates.add(cb.equal(root.get("brand"), brand)); } if (model !=
		 * null) { predicates.add(cb.equal(root.get("model"), model)); } if (userId !=
		 * null) { predicates.add(cb.equal(root.get("updatedBy"), userId)); } if
		 * (subcategory != null) { predicates.add(cb.equal(root.get("subcategory"),
		 * subcategory)); } query.where(predicates.toArray(new Predicate[0]));
		 * 
		 * return entityManager.createQuery(query).getResultList();
		 */
		return root;
	}

	@Override
	public List<UserChargeResponseDTO> getUserCharges(String serviceChargeKey, String serviceId, String location,
	        String brand, String model, String updatedBy, String subcategory) {

	    List<UserServiceCharges> list = repo.searchCharges(
	            serviceChargeKey, serviceId, location, brand, model, subcategory, updatedBy);

	    if (list == null || list.isEmpty()) {
	        return new ArrayList<>();
	    }

	    // 1️⃣ Fetch User (updatedBy = bodSeqNo)
	    User user = userDAO.findByBodSeqNo(updatedBy);

	    String userName = (user != null) ? user.getName() : null;

	    // 2️⃣ Group by unique parent fields (NO updatedDate)
	    Map<String, List<UserServiceCharges>> grouped = list.stream()
	            .collect(Collectors.groupingBy(u ->
	                    u.getLocation() + "|" +
	                    u.getBrand() + "|" +
	                    u.getModel() + "|" +
	                    u.getSubcategory() + "|" +
	                    u.getUpdatedBy()
	            ));

	    List<UserChargeResponseDTO> finalList = new ArrayList<>();

	    for (Map.Entry<String, List<UserServiceCharges>> entry : grouped.entrySet()) {

	        List<UserServiceCharges> groupItems = entry.getValue();
	        UserServiceCharges first = groupItems.get(0);

	        UserChargeResponseDTO parent = new UserChargeResponseDTO();
	        parent.setLocation(first.getLocation());

	        // Take newest updatedDate among children
	        parent.setUpdatedDate(groupItems.stream()
	                .map(UserServiceCharges::getUpdatedDate)
	                .max(String::compareTo)
	                .orElse(first.getUpdatedDate()));

	        parent.setUpdatedBy(first.getUpdatedBy());
	        parent.setBodSeqNo(first.getUpdatedBy());     // same as updatedBy
	        parent.setName(userName);                     // USER NAME FROM USER TABLE
	        parent.setBrand(first.getBrand());
	        parent.setModel(first.getModel());
	        parent.setSubcategory(first.getSubcategory());

	        // Child Records
	        List<UserChargeChildDTO> childList = new ArrayList<>();

	        for (UserServiceCharges e : groupItems) {
	            UserChargeChildDTO child = new UserChargeChildDTO();
	            child.setServiceId(e.getServiceId());
	            child.setServiceName(e.getServiceName());
	            child.setServiceCharge(e.getServiceCharge());
	            child.setServiceChargeKey(e.getServiceChargeKey());
	            childList.add(child);
	        }

	        parent.setCharges(childList);
	        finalList.add(parent);
	    }

	    return finalList;
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
