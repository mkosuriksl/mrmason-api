package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.SpWorkersDto;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.SpWorkersRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SpWorkersService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

//	@Override
//	public List<SpWorkersDto> getWorkers(String spId,String workerId,String phno,String location,String workerAvail) {
//
//        List<SpWorkers> workers = workerRepo.findByServicePersonIdOrWorkerIdOrWorkPhoneNumOrWorkerLocationOrWorkerAvail(
//                                        spId, workerId, phno, location, workerAvail);
//
//        // Mapping entities to DTOs
//        List<SpWorkersDto> dtos = new ArrayList<>();
//        for (SpWorkers workerEntity : workers) {
//        	SpWorkersDto dto = new SpWorkersDto();
//            
//            dto.setServicePersonId(workerEntity.getServicePersonId());
//            dto.setWorkerId(workerEntity.getWorkerId());
//            dto.setWorkPhoneNum(workerEntity.getWorkPhoneNum());
//            dto.setWorkerLocation(workerEntity.getWorkerLocation());
//            dto.setWorkerName(workerEntity.getWorkerName());
//            dto.setWorkerAvail(workerEntity.getWorkerAvail());
//            User data=userRepo.findByBodSeqNo(workerEntity.getServicePersonId());
//			dto.setWorkerStatus(data.getStatus());
//            // Map other attributes as needed
//            dtos.add(dto);
//        }
//
//        return dtos;
//    }
	@Override
	public List<SpWorkers> getWorkers(String spId, String workerId, String phno, String location, String workerAvail) {

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

	    query.select(root);
	    if (!predicates.isEmpty()) {
	        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
	    }

	    return entityManager.createQuery(query).getResultList();

//	    List<SpWorkersDto> dtos = new ArrayList<>();
//	    for (SpWorkers workerEntity : workers) {
//	        SpWorkersDto dto = new SpWorkersDto();
//	        dto.setServicePersonId(workerEntity.getServicePersonId());
//	        dto.setWorkerId(workerEntity.getWorkerId());
//	        dto.setWorkPhoneNum(workerEntity.getWorkPhoneNum());
//	        dto.setWorkerLocation(workerEntity.getWorkerLocation());
//	        dto.setWorkerName(workerEntity.getWorkerName());
//	        dto.setWorkerAvail(workerEntity.getWorkerAvail());
//	        dto.setWorkerEmail(workerEntity.getWorkerEmail());
//	        // Fetch related User status
//	        User data = userRepo.findByBodSeqNo(workerEntity.getServicePersonId());
//	        if (data != null) {
//	            dto.setWorkerStatus(data.getStatus());
//	        }
//
//	        dtos.add(dto);
//	    }

//	    return dtos;
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
			User data=userRepo.findByBodSeqNo(spId);
			data.setStatus(workerStatus);
			userRepo.save(data);
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

}