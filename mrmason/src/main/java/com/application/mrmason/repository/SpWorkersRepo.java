package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.SpWorkers;

@Repository
public interface SpWorkersRepo extends JpaRepository<SpWorkers,String>{
	List<SpWorkers> findByServicePersonIdOrWorkerIdOrWorkPhoneNumOrWorkerLocationOrWorkerAvail(String serviceId, String workId, String workPhoneNum, String workerLocation, String workerAvail);
	SpWorkers findByWorkerIdAndServicePersonId(String workId, String serviceId);
	SpWorkers findByWorkPhoneNum(String workPhoneNum);
	SpWorkers findByWorkerEmail(String workPhoneNum);
	List<SpWorkers> findByWorkerId(String workerId);
	@Query("SELECT s FROM SpWorkers s WHERE s.workerId = :workerId")
	SpWorkers findByWorkerIdOne(@Param("workerId") String workerId);

}
