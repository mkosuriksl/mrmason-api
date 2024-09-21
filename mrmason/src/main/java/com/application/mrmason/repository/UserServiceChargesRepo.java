package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.UserServiceCharges;

@Repository
public interface UserServiceChargesRepo extends JpaRepository<UserServiceCharges, String> {

	@Query("SELECT u FROM UserServiceCharges u WHERE "
			+ "(:serviceChargeKey IS NULL OR u.serviceChargeKey = :serviceChargeKey) AND "
			+ "(:serviceId IS NULL OR u.serviceId = :serviceId) AND "
			+ "(:location IS NULL OR u.location = :location) AND " + "(:brand IS NULL OR u.brand = :brand) AND "
			+ "(:model IS NULL OR u.model = :model) AND u.updatedBy = :updatedBy AND "
			+ "(:subcategory IS NULL OR u.subcategory = :subcategory)")
	List<UserServiceCharges> findbySearchValue(@Param("serviceChargeKey") String serviceChargeKey,
			@Param("serviceId") String serviceId, @Param("location") String location, @Param("brand") String brand,
			@Param("model") String model, @Param("updatedBy") String updatedBy,
			@Param("subcategory") String subcategory);

}
