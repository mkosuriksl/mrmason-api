package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.SpWorkersDto;
import com.application.mrmason.entity.SpWorkers;

public interface SpWorkersService {
	String addWorkers(SpWorkers worker);
//    List<SpWorkersDto> getWorkers(String spId,String workerId,String phno,String location,String workerAvail);
//	List<SpWorkers> getWorkers(String spId,String workerId,String phno,String location,String workerAvail);
	public Page<SpWorkers> getWorkers(String spId, String workerId, String phno, String location, String workerAvail, Pageable pageable);
    String updateWorkers(SpWorkersDto worker); 
    SpWorkersDto getDetails(String phno,String email);
    public SpWorkers getWorkerById(String workerId);
}
