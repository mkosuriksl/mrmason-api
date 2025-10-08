package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.APIKEY;
import com.application.mrmason.repository.APIKeyRepository;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.APIKeyService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class APIKeyServiceImpls implements APIKeyService {

	@Autowired
	private APIKeyRepository repository;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public APIKEY addApiKey(APIKEY apiKey) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		apiKey.setUpdatedBy(loggedInUserEmail);
		apiKey.setUpdatedDate(new Date());

		return repository.save(apiKey);
	}

	@Override
	public APIKEY updateApiKey(APIKEY updatedApiKey) {
		APIKEY existingKey = repository.findById(updatedApiKey.getApiKey())
				.orElseThrow(() -> new RuntimeException("API Key not found: " + updatedApiKey.getApiKey()));

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();

		existingKey.setUpdatedBy(loggedInUserEmail);
		existingKey.setUpdatedDate(new Date());

		return repository.save(existingKey);
	}
	
	@Override
	public Page<APIKEY> get(String apiKey, String updatedBy,Pageable pageable) throws AccessDeniedException {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<APIKEY> query = cb.createQuery(APIKEY.class);
		Root<APIKEY> root = query.from(APIKEY.class);
		List<Predicate> predicates = new ArrayList<>();

		if (apiKey != null && !apiKey.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("apiKey"), apiKey));
		}
		if (updatedBy != null && !updatedBy.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<APIKEY> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<APIKEY> countRoot = countQuery.from(APIKEY.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (apiKey != null && !apiKey.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("apiKey"), apiKey));
		}
		if (updatedBy != null && !updatedBy.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
		}
		
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}


}
