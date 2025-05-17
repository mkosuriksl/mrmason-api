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

import com.application.mrmason.dto.MaterialRequestMr;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.MaterialRequirementByRequest;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.MaterialRequestMRRepository;
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
	
	@Autowired
	private MaterialRequestMRRepository mrRepository;
	
	@Autowired
	private CustomerRegistrationRepo customerRegistrationRepo;
	
	@Autowired
	public AdminDetailsRepo adminRepo;

	@Override
	public List<MaterialRequirementByRequest> createMaterialRequirementByRequest(
	        List<MaterialRequirementByRequest> requestList, RegSource regSource) {

	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

	    List<String> roleNames = loggedInRole.stream()
	        .map(GrantedAuthority::getAuthority)
	        .map(role -> role.replace("ROLE_", ""))
	        .collect(Collectors.toList());

	    if (roleNames.equals("Developer")) {
	        throw new ResourceNotFoundException("Restricted role: " + roleNames);
	    }

	    UserType userType = UserType.valueOf(roleNames.get(0));

	    // User identity variables
	    String userId;
	    String spId;

	    if (userType == UserType.EC) {
	        // EC User
	        CustomerRegistration customer = customerRegistrationRepo
	            .findByUserEmailAndUserType(loggedInUserEmail, userType)
	            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));
	        
	        userId = customer.getUserid();
	        spId = customer.getUserid();

	    } else if (userType == UserType.Adm) {
	        // Admin User
	        AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail,userType)
	            .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
	        
	        userId = admin.getEmail();
	        spId = admin.getEmail(); // or any other logic you want to follow

	    } else {
	        // Other user types
	        User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
	        
	        userId = user.getBodSeqNo();
	        spId = user.getBodSeqNo(); // modify if needed
	    }

	    List<MaterialRequirementByRequest> savedList = new ArrayList<>();
	    String reqId = null;
	    boolean isNewReqId = false;

	    boolean hasNullReqId = requestList.stream()
	        .anyMatch(req -> req.getReqId() == null || req.getReqId().trim().isEmpty());

	    if (hasNullReqId) {
	        reqId = "MR" + System.currentTimeMillis();
	        isNewReqId = true;
	    }

	    int lineCounter = 1;

	    for (MaterialRequirementByRequest req : requestList) {
	        String currentReqId = req.getReqId();

	        if (currentReqId == null || currentReqId.trim().isEmpty()) {
	            currentReqId = reqId;
	        } else {
	            SiteMeasurement siteMeasurement = siteRepository.findByServiceRequestId(currentReqId);
	            if (siteMeasurement == null) {
	                throw new ResourceNotFoundException("SiteMeasurement not found for reqId: " + currentReqId);
	            }
	        }

	        String lineIdSuffix = String.format("_%04d", lineCounter++);
	        String reqIdLineId = currentReqId + lineIdSuffix;

	        req.setReqId(currentReqId);
	        req.setReqIdLineId(reqIdLineId);
	        req.setUpdatedBy(userId);
	        req.setUpdatedDate(new Date());
	        req.setSpId(spId);

	        MaterialRequirementByRequest saved = repository.save(req);
	        savedList.add(saved);

	        if (isNewReqId) {
	            MaterialRequestMr mr = new MaterialRequestMr();
	            mr.setReqId(currentReqId);
	            mr.setReqIdLineId(reqIdLineId);
	            mr.setCreatedDate(new Date());
	            mrRepository.save(mr);
	        }
	    }

	    return savedList;
	}


	@Override
	public List<MaterialRequirementByRequest> updateMaterialRequirementByRequest(
			List<MaterialRequirementByRequest> materialRequirementByRequest, RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
				.collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
	        throw new ResourceNotFoundException("Restricted role: " + roleNames);
	    }

	    UserType userType = UserType.valueOf(roleNames.get(0));

	    // User identity variables
	    String userId;
	    String spId;

	    if (userType == UserType.EC) {
	        // EC User
	        CustomerRegistration customer = customerRegistrationRepo
	            .findByUserEmailAndUserType(loggedInUserEmail, userType)
	            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));
	        
	        userId = customer.getUserid();

	    } else if (userType == UserType.Adm) {
	        // Admin User
	        AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail,userType)
	            .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
	        
	        userId = admin.getEmail();// or any other logic you want to follow

	    } else {
	        // Other user types
	        User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
	        
	        userId = user.getBodSeqNo(); // modify if needed
	    }


		List<MaterialRequirementByRequest> updatedList = new ArrayList<>();
		for (MaterialRequirementByRequest request : materialRequirementByRequest) {
			MaterialRequirementByRequest existing = repository.findById(request.getReqIdLineId())
					.orElseThrow(() -> new EntityNotFoundException(
							"Work assignment not found with recId: " + request.getReqIdLineId()));

//			existing.setUpdatedBy(user.getBodSeqNo());
			existing.setUpdatedBy(userId);
			existing.setUpdatedDate(new Date()); // Set current date/time
			existing.setStatus(request.getStatus());
			existing.setAmount(request.getAmount());
			existing.setMaterialCategory(request.getMaterialCategory());
			existing.setBrand(request.getBrand());
			existing.setItemName(request.getItemName());
			existing.setShape(request.getShape());
			existing.setModelName(request.getModelName());
			existing.setModelCode(request.getModelCode());
			existing.setSizeInInch(request.getSizeInInch());
			existing.setLength(request.getLength());
			existing.setLengthInUnit(request.getLengthInUnit());
			existing.setWidth(request.getWidth());
			existing.setWidthInUnit(request.getWidthInUnit());
			existing.setThickness(request.getThickness());
			existing.setThicknessInUnit(request.getThicknessInUnit());
			existing.setNoOfItems(request.getNoOfItems());
			existing.setWeightInKgs(request.getWeightInKgs());
			existing.setGst(request.getGst());
			existing.setTotalAmount(request.getTotalAmount());
//		return repository.save(existing);
			updatedList.add(repository.save(existing));
		}
		return updatedList;
	}

	@Override

	public Page<MaterialRequirementByRequest> getMaterialRequirementByRequest(String materialCategory, String brand,
			String itemName, String reqIdLineId, String modelName, String modelCode, String reqId, String spId,
			String updatedBy, String status, Pageable pageable, RegSource regSource) {

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
