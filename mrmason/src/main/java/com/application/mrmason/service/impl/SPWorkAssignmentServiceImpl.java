package com.application.mrmason.service.impl;

import java.time.LocalDate;
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

import com.application.mrmason.dto.SPWorkerAssignmentDTO;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.SPWorkAssignment;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.SPWorkAssignmentRepository;
import com.application.mrmason.repository.SpWorkersRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SPWorkAssignmentService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;



@Service
public class SPWorkAssignmentServiceImpl implements SPWorkAssignmentService {

	@Autowired
	private SPWorkAssignmentRepository repository;
	
	@Autowired
	SpWorkersRepo workerRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	UserDAO userDAO;

	@Override
	public List<SPWorkAssignment> createAssignment(SPWorkAssignment assignment,RegSource regSource) {

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		System.out.println("ROLE"+loggedInUserEmail);
		List<String> roleNames = loggedInRole.stream()
		        .map(GrantedAuthority::getAuthority)
		        .map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
		        .collect(Collectors.toList());

		if (roleNames.equals("Developer")||roleNames.equals("Adm")) {
		    throw new ResourceNotFoundException("Role Developer not found in: " + roleNames);
		}
		UserType userType = UserType.valueOf(roleNames.get(0)); // Make sure roleNames is not empty
		User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType,regSource)
		    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
		
	    List<SpWorkers> spWorkerList = workerRepo.findByWorkerId(assignment.getWorkerId());
	    if (spWorkerList.isEmpty()) {
	        throw new ResourceNotFoundException("Worker not found with ID: " + assignment.getWorkerId());
	    }

	    SpWorkers spWorker = spWorkerList.get(0);
	    LocalDate start = LocalDate.parse(assignment.getDateOfWork());
	    LocalDate end = LocalDate.parse(assignment.getEndDateOfWork());

	    List<SPWorkAssignment> assignmentsToSave = new ArrayList<>();

	    for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
	        SPWorkAssignment newAssignment = new SPWorkAssignment();

	        newAssignment.setWorkerId(spWorker.getWorkerId());
	        newAssignment.setWorkOrdId(assignment.getWorkOrdId());
	        newAssignment.setDateOfWork(date.toString());
	        newAssignment.setEndDateOfWork(assignment.getEndDateOfWork());
	        newAssignment.setUpdatedBy(user.getBodSeqNo());
	        newAssignment.setUpdatedDate(new Date());
	        newAssignment.setCurrency("INR");
	        newAssignment.setStatus(SPWAStatus.NEW);
	        newAssignment.setSpId(user.getBodSeqNo());
	        newAssignment.setAmount(assignment.getAmount());
	        newAssignment.setPaymentStatus(assignment.getPaymentStatus());
	        newAssignment.setPaymentMethod(assignment.getPaymentMethod());
	        newAssignment.setLocation(assignment.getLocation());
	        newAssignment.setAvailable(assignment.getAvailable());

//	        // Generate unique recId
//	        String recId = "SPWA_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
//	        newAssignment.setRecId(recId);
//	        newAssignment.setWorkerIdWorkOrdIdLine(spWorker.getWorkerId() + "_" + assignment.getWorkOrdId() + "_" + "0001");

	        assignmentsToSave.add(newAssignment);
	    }

	    return repository.saveAll(assignmentsToSave);
	}

//	public List<SPWorkAssignment> getWorkers(String recId,String servicePersonId, String workerId, String updatedBy) {
//
//	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//	    CriteriaQuery<SPWorkAssignment> query = cb.createQuery(SPWorkAssignment.class);
//	    Root<SPWorkAssignment> root = query.from(SPWorkAssignment.class);
//
//	    List<Predicate> predicates = new ArrayList<>();
//
//	    if (recId != null && !recId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("recId"), recId));
//	    }
//	    if (servicePersonId != null && !servicePersonId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("servicePersonId"), servicePersonId));
//	    }
//	    if (workerId != null && !workerId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("workerId"), workerId));
//	    }
//	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
//	    }
//
//	    query.select(root);
//	    if (!predicates.isEmpty()) {
//	        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
//	    }
//
//	    return entityManager.createQuery(query).getResultList();
//	}

	@Override
	public SPWorkAssignment updateWorkAssignment(SPWorkAssignment updatedAssignment,RegSource regSource) {
		
//		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//
//	    User loginEmail = userDAO.findByEmailOne(loggedInUserEmail)
//	        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		System.out.println("ROLE"+loggedInUserEmail);
		List<String> roleNames = loggedInRole.stream()
		        .map(GrantedAuthority::getAuthority)
		        .map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
		        .collect(Collectors.toList());

		if (roleNames.equals("Developer")||roleNames.equals("Adm")) {
		    throw new ResourceNotFoundException("Role Developer not found in: " + roleNames);
		}
		UserType userType = UserType.valueOf(roleNames.get(0)); // Make sure roleNames is not empty
		User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType,regSource)
		    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
	    SPWorkAssignment existing = repository.findById(updatedAssignment.getRecId())
	        .orElseThrow(() -> new EntityNotFoundException("Work assignment not found with recId: " + updatedAssignment.getRecId()));

	    // Update fields
//	    existing.setServicePersonId(updatedAssignment.getServicePersonId());
//	    existing.setWorkerId(updatedAssignment.getWorkerId());
	    existing.setDateOfWork(updatedAssignment.getDateOfWork());
	    existing.setUpdatedBy(user.getBodSeqNo());
	    existing.setUpdatedDate(new Date()); // Set current date/time
	    existing.setAmount(updatedAssignment.getAmount());
	    existing.setPaymentStatus(updatedAssignment.getPaymentStatus());
	    existing.setPaymentMethod(updatedAssignment.getPaymentMethod());
	    existing.setStatus(updatedAssignment.getStatus());
	    existing.setAvailable(updatedAssignment.getAvailable());
	    existing.setLocation(updatedAssignment.getLocation());
	    
	    return repository.save(existing);
	}

//	@Override
//	public Page<SPWorkAssignment> getWorkers(String recId, String workerIdWorkOrdIdLine, String workerId, String updatedBy,
//	        String location,String available,String fromDateOfWork,String toDateOfWork,String spId,Pageable pageable) {
//
//	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//
//	    // === Main query ===
//	    CriteriaQuery<SPWorkAssignment> query = cb.createQuery(SPWorkAssignment.class);
//	    Root<SPWorkAssignment> root = query.from(SPWorkAssignment.class);
//	    List<Predicate> predicates = new ArrayList<>();
//
//	    if (recId != null && !recId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("recId"), recId));
//	    }
//	    if (workerIdWorkOrdIdLine != null && !workerIdWorkOrdIdLine.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("workerIdWorkOrdIdLine"), workerIdWorkOrdIdLine));
//	    }
//	    if (workerId != null && !workerId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("workerId"), workerId));
//	    }
//	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
//	    }
//	    if (location != null && !location.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("location"), location));
//	    }
//	    if (available != null && !available.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("available"), available));
//	    }
//	    if (fromDateOfWork != null && toDateOfWork != null) {
//			predicates.add(cb.between(root.get("dateOfWork"), fromDateOfWork, toDateOfWork));
//		} else if (fromDateOfWork != null) {
//			predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfWork"), fromDateOfWork));
//		} else if (toDateOfWork != null) {
//			predicates.add(cb.lessThanOrEqualTo(root.get("dateOfWork"), toDateOfWork));
//		}
//	    if (spId != null && !spId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("spId"), spId));
//	    }
//		
//	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
//	    TypedQuery<SPWorkAssignment> typedQuery = entityManager.createQuery(query);
//	    typedQuery.setFirstResult((int) pageable.getOffset());
//	    typedQuery.setMaxResults(pageable.getPageSize());
//
//	    // === Count query ===
//	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//	    Root<SPWorkAssignment> countRoot = countQuery.from(SPWorkAssignment.class);
//	    List<Predicate> countPredicates = new ArrayList<>();
//
//	    if (recId != null && !recId.trim().isEmpty()) {
//	        countPredicates.add(cb.equal(countRoot.get("recId"), recId));
//	    }
//	    if (workerIdWorkOrdIdLine != null && !workerIdWorkOrdIdLine.trim().isEmpty()) {
//	        countPredicates.add(cb.equal(countRoot.get("workerIdWorkOrdIdLine"), workerIdWorkOrdIdLine));
//	    }
//	    if (workerId != null && !workerId.trim().isEmpty()) {
//	        countPredicates.add(cb.equal(countRoot.get("workerId"), workerId));
//	    }
//	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//	        countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
//	    }
//	    if (location != null && !location.trim().isEmpty()) {
//	    	countPredicates.add(cb.equal(countRoot.get("location"), location));
//	    }
//	    if (available != null && !available.trim().isEmpty()) {
//	    	countPredicates.add(cb.equal(countRoot.get("available"), available));
//	    }
//	    if (fromDateOfWork != null && toDateOfWork != null) {
//			countPredicates.add(cb.between(countRoot.get("dateOfWork"), fromDateOfWork, toDateOfWork));
//		} else if (fromDateOfWork != null) {
//			countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("dateOfWork"), fromDateOfWork));
//		} else if (toDateOfWork != null) {
//			countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("dateOfWork"), toDateOfWork));
//		}
//	    if (spId != null && !spId.trim().isEmpty()) {
//	    	countPredicates.add(cb.equal(countRoot.get("spId"), spId));
//	    }
//	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
//	    Long total = entityManager.createQuery(countQuery).getSingleResult();
//
//	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
//	}

	public Page<SPWorkerAssignmentDTO> getWorkers(
	    String recId, String workerIdWorkOrdIdLine, String workerId, String updatedBy,
	    String location, String available, String fromDateOfWork, String toDateOfWork,
	    String spId, Pageable pageable) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // Main query selecting fields from both SPWorkAssignment and Worker
	    CriteriaQuery<Tuple> query = cb.createTupleQuery();
	    Root<SPWorkAssignment> spRoot = query.from(SPWorkAssignment.class);
	    Root<SpWorkers> workerRoot = query.from(SpWorkers.class);

	    // Join condition on workerId
	    Predicate joinCondition = cb.equal(spRoot.get("workerId"), workerRoot.get("workerId"));

	    List<Predicate> predicates = new ArrayList<>();
	    predicates.add(joinCondition);

	    if (recId != null && !recId.trim().isEmpty()) {
	        predicates.add(cb.equal(spRoot.get("recId"), recId));
	    }
	    if (workerIdWorkOrdIdLine != null && !workerIdWorkOrdIdLine.trim().isEmpty()) {
	        predicates.add(cb.equal(spRoot.get("workerIdWorkOrdIdLine"), workerIdWorkOrdIdLine));
	    }
	    if (workerId != null && !workerId.trim().isEmpty()) {
	        predicates.add(cb.equal(spRoot.get("workerId"), workerId));
	    }
	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
	        predicates.add(cb.equal(spRoot.get("updatedBy"), updatedBy));
	    }
	    if (location != null && !location.trim().isEmpty()) {
	        predicates.add(cb.equal(spRoot.get("location"), location));
	    }
	    if (available != null && !available.trim().isEmpty()) {
	        predicates.add(cb.equal(spRoot.get("available"), available));
	    }
	    if (fromDateOfWork != null && toDateOfWork != null) {
	        predicates.add(cb.between(spRoot.get("dateOfWork"), fromDateOfWork, toDateOfWork));
	    } else if (fromDateOfWork != null) {
	        predicates.add(cb.greaterThanOrEqualTo(spRoot.get("dateOfWork"), fromDateOfWork));
	    } else if (toDateOfWork != null) {
	        predicates.add(cb.lessThanOrEqualTo(spRoot.get("dateOfWork"), toDateOfWork));
	    }
	    if (spId != null && !spId.trim().isEmpty()) {
	        predicates.add(cb.equal(spRoot.get("spId"), spId));
	    }

	    query.multiselect(spRoot, workerRoot.get("workerName"), workerRoot.get("workPhoneNum"))
	         .where(cb.and(predicates.toArray(new Predicate[0])));

	    TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    List<Tuple> results = typedQuery.getResultList();

	    List<SPWorkerAssignmentDTO> dtos = new ArrayList<>();
	    for (Tuple tuple : results) {
	        SPWorkAssignment assignment = tuple.get(spRoot);
	        String workerName = tuple.get(workerRoot.get("workerName"));
	        String workPhoneNum = tuple.get(workerRoot.get("workPhoneNum"));
	        dtos.add(new SPWorkerAssignmentDTO(assignment, workerName, workPhoneNum));
	    }

	    // Count query for pagination
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<SPWorkAssignment> countRoot = countQuery.from(SPWorkAssignment.class);
	    List<Predicate> countPredicates = new ArrayList<>();
	    if (recId != null && !recId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("recId"), recId));
	    }
	    if (workerIdWorkOrdIdLine != null && !workerIdWorkOrdIdLine.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workerIdWorkOrdIdLine"), workerIdWorkOrdIdLine));
	    }
	    if (workerId != null && !workerId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workerId"), workerId));
	    }
	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
	    }
	    if (location != null && !location.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("location"), location));
	    }
	    if (available != null && !available.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("available"), available));
	    }
	    if (fromDateOfWork != null && toDateOfWork != null) {
	        countPredicates.add(cb.between(countRoot.get("dateOfWork"), fromDateOfWork, toDateOfWork));
	    } else if (fromDateOfWork != null) {
	        countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("dateOfWork"), fromDateOfWork));
	    } else if (toDateOfWork != null) {
	        countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("dateOfWork"), toDateOfWork));
	    }
	    if (spId != null && !spId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("spId"), spId));
	    }

	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(dtos, pageable, total);
	}
}
