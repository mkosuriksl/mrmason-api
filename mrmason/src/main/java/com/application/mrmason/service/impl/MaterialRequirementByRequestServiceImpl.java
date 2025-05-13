package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.MaterialRequirementByRequest;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.MaterialRequirementByRequestRepository;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.MaterialRequirementByRequestService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class MaterialRequirementByRequestServiceImpl implements MaterialRequirementByRequestService {

	@Autowired
	private MaterialRequirementByRequestRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	UserDAO userDAO;

	@Autowired
	private SiteMeasurementRepository siteRepository;

	@Override
	public MaterialRequirementByRequest createMaterialRequirementByRequest(
			MaterialRequirementByRequest materialRequirementByRequest, RegSource regSource) {

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		System.out.println("ROLE" + loggedInUserEmail);
		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
				.collect(Collectors.toList());

		if (roleNames.equals("Developer") || roleNames.equals("Adm")) {
			throw new ResourceNotFoundException("Role Developer not found in: " + roleNames);
		}
		UserType userType = UserType.valueOf(roleNames.get(0)); // Make sure roleNames is not empty
		User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

		SiteMeasurement siteMeasurement = siteRepository
				.findByServiceRequestId(materialRequirementByRequest.getReqId());
		if (siteMeasurement == null) {
			throw new ResourceNotFoundException(
					"SiteMeasurement not found for reqId: " + materialRequirementByRequest.getReqId());
		}

		String reqId = siteMeasurement.getServiceRequestId();

		// === Generate reqIdLineId ===
		int count = repository.countByReqId(reqId); // You need to implement this in your repository
		String lineIdSuffix = String.format("_%04d", count + 1);
		String reqIdLineId = reqId + lineIdSuffix;

		// === Set values ===
		materialRequirementByRequest.setReqId(reqId);
		materialRequirementByRequest.setReqIdLineId(reqIdLineId);
		materialRequirementByRequest.setUpdatedBy(user.getBodSeqNo());
		materialRequirementByRequest.setUpdatedDate(new Date());
		materialRequirementByRequest.setSpId(user.getBodSeqNo());

		return repository.save(materialRequirementByRequest);
	}

	@Override
	public MaterialRequirementByRequest updateMaterialRequirementByRequest(
			MaterialRequirementByRequest materialRequirementByRequest, RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
				.collect(Collectors.toList());

		if (roleNames.equals("Developer") || roleNames.equals("Adm")) {
			throw new ResourceNotFoundException("Role Developer not found in: " + roleNames);
		}
		UserType userType = UserType.valueOf(roleNames.get(0)); // Make sure roleNames is not empty
		User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
		MaterialRequirementByRequest existing = repository.findById(materialRequirementByRequest.getReqIdLineId())
				.orElseThrow(() -> new EntityNotFoundException(
						"Work assignment not found with recId: " + materialRequirementByRequest.getReqIdLineId()));

		existing.setUpdatedBy(user.getBodSeqNo());
		existing.setUpdatedDate(new Date()); // Set current date/time
		existing.setStatus(materialRequirementByRequest.getStatus());
		existing.setAmount(materialRequirementByRequest.getAmount());
		existing.setMaterialCategory(materialRequirementByRequest.getMaterialCategory());
		existing.setBrand(materialRequirementByRequest.getBrand());
		existing.setItemName(materialRequirementByRequest.getItemName());
		existing.setShape(materialRequirementByRequest.getShape());
		existing.setModelName(materialRequirementByRequest.getModelName());
		existing.setModelCode(materialRequirementByRequest.getModelCode());
		existing.setSizeInInch(materialRequirementByRequest.getSizeInInch());
		existing.setLength(materialRequirementByRequest.getLength());
		existing.setLengthInUnit(materialRequirementByRequest.getLengthInUnit());
		existing.setWidth(materialRequirementByRequest.getWidth());
		existing.setWidthInUnit(materialRequirementByRequest.getWidthInUnit());
		existing.setThickness(materialRequirementByRequest.getThickness());
		existing.setThicknessInUnit(materialRequirementByRequest.getThicknessInUnit());
		existing.setNoOfItems(materialRequirementByRequest.getNoOfItems());
		existing.setWeightInKgs(materialRequirementByRequest.getWeightInKgs());
		existing.setGst(materialRequirementByRequest.getGst());
		existing.setTotalAmount(materialRequirementByRequest.getTotalAmount());
		return repository.save(existing);
	}

	@Override

	public Page<MaterialRequirementByRequest> getMaterialRequirementByRequest(String materialCategory, String brand,
			String itemName, String reqIdLineId, String modelName, String modelCode, String reqId, String spId,
			String updatedBy, String status, Pageable pageable,RegSource regSource) {
		
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
				.collect(Collectors.toList());

		if (roleNames.equals("Developer") || roleNames.equals("Adm")) {
			throw new ResourceNotFoundException("Role Developer not found in: " + roleNames);
		}
		UserType userType = UserType.valueOf(roleNames.get(0)); // Make sure roleNames is not empty
		User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<MaterialRequirementByRequest> query = cb.createQuery(MaterialRequirementByRequest.class);
		Root<MaterialRequirementByRequest> root = query.from(MaterialRequirementByRequest.class);
		List<Predicate> predicates = new ArrayList<>();

		if (reqIdLineId != null && !reqIdLineId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("reqIdLineId"), reqIdLineId));
		}
		if (materialCategory != null && !materialCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("materialCategory"), materialCategory));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (itemName != null && !itemName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemName"), itemName));
		}
		if (modelName != null && !modelName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelName"), modelName));
		}
		if (modelCode != null && !modelCode.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelCode"), modelCode));
		}
		if (reqId != null && !reqId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("reqId"), reqId));
		}
		if (spId != null && !spId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("spId"), spId));
		}
		if (updatedBy != null && !updatedBy.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
		if (status != null && !status.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("status"), status));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<MaterialRequirementByRequest> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// === Count query ===
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialRequirementByRequest> countRoot = countQuery.from(MaterialRequirementByRequest.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (reqIdLineId != null && !reqIdLineId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("reqIdLineId"), reqIdLineId));
		}
		if (materialCategory != null && !materialCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("materialCategory"), materialCategory));
		}
		if (brand != null && !brand.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("brand"), brand));
		}
		if (itemName != null && !itemName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("itemName"), itemName));
		}
		if (modelName != null && !modelName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelName"), modelName));
		}
		if (modelCode != null && !modelCode.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("modelCode"), modelCode));
		}
		if (reqId != null && !reqId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("reqId"), reqId));
		}
		if (spId != null && !spId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("spId"), spId));
		}
		if (updatedBy != null && !updatedBy.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
		if (status != null && !status.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("status"), status));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

}
