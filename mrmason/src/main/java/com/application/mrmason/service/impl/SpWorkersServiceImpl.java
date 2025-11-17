package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.SpWorkersDto;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.SpWorkersRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SpWorkersService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


@Service
public class SpWorkersServiceImpl implements SpWorkersService {

	@Autowired
	UserDAO userRepo;
	@Autowired
	SpWorkersRepo workerRepo;
	@Autowired
	ServicePersonLoginDAO spRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public String addWorkers(SpWorkers worker) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
		Optional<User> userData = Optional.ofNullable(userRepo.findByBodSeqNo(worker.getServicePersonId()));
		if (userData.isPresent()) {
			SpWorkers workerDetails= workerRepo.findByWorkPhoneNum(worker.getWorkPhoneNum());
			if ( workerDetails== null) {
				SpWorkers spworker = workerRepo.save(worker);
				User user = new User();
//				user.setBodSeqNo(spworker.getWorkerId());
				user.setBodSeqNo(spworker.getWorkerId());
				user.setMobile(worker.getWorkPhoneNum());
				user.setEmail(worker.getWorkerEmail());
				user.setName(worker.getWorkerName());
				user.setVerified("yes");
				user.setLocation(worker.getWorkerLocation());
//				user.setEmail("none");
				user.setEmail(worker.getWorkerEmail());
				user.setStatus("active");
				UserType userType = UserType.fromString("Worker");
				user.setUserType(userType);
				user.setServiceCategory(userData.get().getServiceCategory());
				user.setRegSource(userData.get().getRegSource());
				String encodedPass = byCrypt.encode("mrmason@123");
				user.setPassword(encodedPass);
				userRepo.save(user);

				ServicePersonLogin spDetails = new ServicePersonLogin();
				spDetails.setMobile(spworker.getWorkPhoneNum());
				spDetails.setEmail(worker.getWorkerEmail());
			
//				spDetails.setEmail("none");
				spDetails.setMobVerify("yes");
				spDetails.setEVerify("yes");
				spDetails.setRegSource(userData.get().getRegSource());
				spRepo.save(spDetails);
				return "added";
			}
			return "notUnique";
		}

		return null;
	}
	@Override
	public Page<SpWorkers> getWorkers(String spId, String workerId, String phno, String location, String workerAvail, Pageable pageable) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // === Selection query ===
	    CriteriaQuery<SpWorkers> query = cb.createQuery(SpWorkers.class);
	    Root<SpWorkers> root = query.from(SpWorkers.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (spId != null && !spId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("servicePersonId"), spId));
	    }
	    if (workerId != null && !workerId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workerId"), workerId));
	    }
	    if (phno != null && !phno.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workPhoneNum"), phno));
	    }
	    if (location != null && !location.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workerLocation"), location));
	    }
	    if (workerAvail != null && !workerAvail.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workerAvail"), workerAvail));
	    }

	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	    TypedQuery<SpWorkers> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // === Count query ===
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<SpWorkers> countRoot = countQuery.from(SpWorkers.class);
	    List<Predicate> countPredicates = new ArrayList<>();

	    if (spId != null && !spId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("servicePersonId"), spId));
	    }
	    if (workerId != null && !workerId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workerId"), workerId));
	    }
	    if (phno != null && !phno.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workPhoneNum"), phno));
	    }
	    if (location != null && !location.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workerLocation"), location));
	    }
	    if (workerAvail != null && !workerAvail.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workerAvail"), workerAvail));
	    }

	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Override
	public String updateWorkers(SpWorkersDto worker) {
		String spId = worker.getServicePersonId();
		String workerId = worker.getWorkerId();
		String location = worker.getWorkerLocation();
		String workerAvail = worker.getWorkerAvail();
		String workerStatus = worker.getWorkerStatus();
		
		Optional<SpWorkers> user = Optional.of(workerRepo.findByWorkerIdAndServicePersonId(workerId, spId));
		if (user.isPresent()) {
			user.get().setWorkerLocation(location);
			user.get().setWorkerAvail(workerAvail);
			workerRepo.save(user.get());
			return "updated";
		}
		return null;
	}

	@Override
	public SpWorkersDto getDetails(String phno,String email) {
		SpWorkers workerDetails= workerRepo.findByWorkPhoneNum(phno);
		SpWorkers workerEmail= workerRepo.findByWorkerEmail(email);
		SpWorkersDto spWorker=new SpWorkersDto();
		spWorker.setServicePersonId(workerDetails.getServicePersonId());
		spWorker.setWorkerId(workerDetails.getWorkerId());
		spWorker.setWorkerAvail(workerDetails.getWorkerAvail());
		spWorker.setWorkerName(workerDetails.getWorkerName());
		spWorker.setWorkerLocation(workerDetails.getWorkerLocation());
		spWorker.setWorkPhoneNum(workerDetails.getWorkPhoneNum());
		spWorker.setWorkerEmail(workerEmail.getWorkerEmail());
		User data=userRepo.findByEmailOrMobile(phno, phno);
		spWorker.setWorkerStatus(data.getStatus());
		return spWorker;
	}
	
	@Override
	public SpWorkers getWorkerById(String workerId) {
        return workerRepo.findByWorkerIdOne(workerId);
    }

	@Override
	public List<SpWorkers> getWorkersWithoutPagination(
	        String spId, String workerId, String phno, String location, String workerAvail,String workerName) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<SpWorkers> query = cb.createQuery(SpWorkers.class);
	    Root<SpWorkers> root = query.from(SpWorkers.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (spId != null && !spId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("servicePersonId"), spId));
	    }
	    if (workerId != null && !workerId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workerId"), workerId));
	    }
	    if (phno != null && !phno.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workPhoneNum"), phno));
	    }
	    if (location != null && !location.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workerLocation"), location));
	    }
	    if (workerAvail != null && !workerAvail.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workerAvail"), workerAvail));
	    }
	    if (workerName != null && !workerName.trim().isEmpty()) {
	        predicates.add(cb.like(cb.lower(root.get("workerName")), workerName.toLowerCase() + "%"));
	    }
	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	    return entityManager.createQuery(query).getResultList();
	}

}