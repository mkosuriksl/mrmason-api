package com.applicaion.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.applicaion.mrmason.entity.AddServices;
import com.applicaion.mrmason.entity.User;

@Repository
public interface AddServiceRepo extends JpaRepository<AddServices, String> {

	List<AddServices> findByServiceSubCategory(String serviceSubCategory);

	List<AddServices> findByBodSeqNo(String bodSeqNo);

	AddServices findByUserIdServiceId(String userIdServiceId);

	@Query("SELECT a FROM AddServices a WHERE a.userIdServiceId = :userIdServiceId")
	List<AddServices> getUserIdServiceIdDetails(String userIdServiceId);

}
