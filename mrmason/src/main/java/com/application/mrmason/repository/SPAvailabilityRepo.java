package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.SPAvailability;

@Repository
public interface SPAvailabilityRepo extends JpaRepository<SPAvailability, Integer> {

	@Query("SELECT s FROM SPAvailability s WHERE s.bodSeqNo = :bodSeqNo AND s.availability = 'yes'")
	List<SPAvailability> findByBodSeqNo(String bodSeqNo);

	@Query("SELECT s FROM SPAvailability s WHERE s.bodSeqNo = :bodSeqNo AND s.availability = 'yes'")
	Page<SPAvailability> findByBodSeqNo(String bodSeqNo, Pageable pageable);
	
	@Query("SELECT s FROM SPAvailability s WHERE s.bodSeqNo = :bodSeqNo")
	Optional<SPAvailability> findByBodSeqNos(@Param("bodSeqNo") String bodSeqNo);


}
