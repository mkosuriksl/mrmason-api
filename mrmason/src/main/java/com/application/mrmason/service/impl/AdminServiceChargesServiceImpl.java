package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.application.mrmason.entity.AdminServiceCharges;
import com.application.mrmason.repository.AdminServiceChargesRepo;
import com.application.mrmason.service.AdminServiceChargesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

	@Override
	public AdminServiceCharges addCharges(AdminServiceCharges charges) {

		charges.setServiceChargeKey(generateServiceChargeKey(charges.getServiceId(), charges.getLocation(),
				charges.getBrand(), charges.getModel()));

		Optional<AdminServiceCharges> existingCharges = repo.findById(charges.getServiceChargeKey());

		if (!existingCharges.isPresent()) {
			repo.save(charges);
			return charges;
		}

		return null;
	}

	private String generateServiceChargeKey(String serviceId, String location, String brand, String model) {
		String subString = location.substring(0, Math.min(4, location.length()));
		return serviceId + "_" + brand + "_" + model + "_" + subString;
	}

	@Override
	public List<AdminServiceCharges> getAdminServiceCharges(String serviceChargeKey, String serviceId, String location,
			String brand, String model) {
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

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
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
