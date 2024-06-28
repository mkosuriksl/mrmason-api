package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.application.mrmason.entity.AdminAmcRate;
import com.application.mrmason.repository.AdminAmcRateRepo;
import com.application.mrmason.service.AdminAmcRateService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminAmcRateServiceImpl implements AdminAmcRateService {

	@Autowired
	private AdminAmcRateRepo amcRepo;

	@PersistenceContext
	EntityManager entityManager;

	public AdminAmcRate addAdminamc(AdminAmcRate amc) {
		String amcId = generateAmcId(amc); // Assuming you have a method to generate amcId
		amc.setAmcId(amcId);

		Optional<AdminAmcRate> amcIdExists = amcRepo.findByAmcIdCustom(amc.getAmcId());
		if (!amcIdExists.isPresent()) {
			return amcRepo.save(amc);
		}
		return null;
	}

	private String generateAmcId(AdminAmcRate amc) {
		String subString = amc.getLocation().substring(0, Math.min(3, amc.getLocation().length()));

		return String.join("_", amc.getAssetBrand(), amc.getAssetModel(), amc.getAssetSubCat(), subString,
				amc.getPlanId());
	}

	@Override
	public List<AdminAmcRate> getAmcRates(String amcId, String planId, String assetSubCat, String assetModel,
			String assetBrand) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminAmcRate> query = cb.createQuery(AdminAmcRate.class);
		Root<AdminAmcRate> root = query.from(AdminAmcRate.class);
		List<Predicate> predicates = new ArrayList<>();

		if (amcId != null) {
			predicates.add(cb.equal(root.get("amcId"), amcId));
		}
		if (planId != null) {
			predicates.add(cb.equal(root.get("planId"), planId));
		}
		if (assetSubCat != null) {
			predicates.add(cb.equal(root.get("assetSubCat"), assetSubCat));
		}
		if (assetModel != null) {
			predicates.add(cb.equal(root.get("assetModel"), assetModel));
		}
		if (assetBrand != null) {
			predicates.add(cb.equal(root.get("assetBrand"), assetBrand));
		}

		query.where(predicates.toArray(new Predicate[0]));

		List<AdminAmcRate> result = entityManager.createQuery(query).getResultList();
		return result != null ? result : new ArrayList<>();
	}

	@Override
	public AdminAmcRate updateAmcRates(AdminAmcRate amc) {
		String amcId = amc.getAmcId();
		String amount = amc.getAmount();

		Optional<AdminAmcRate> adminAmc = Optional.ofNullable(amcRepo.findByAmcId(amcId));
		if (adminAmc.isPresent()) {
			adminAmc.get().setAmount(amount);
			return amcRepo.save(adminAmc.get());
		} else {
			return null;
		}

	}

}