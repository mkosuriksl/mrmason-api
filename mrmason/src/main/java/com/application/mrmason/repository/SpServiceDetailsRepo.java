package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.SpServiceDetails;

@Repository
public interface SpServiceDetailsRepo extends JpaRepository<SpServiceDetails, String> {
	List<SpServiceDetails> findByUserIdOrServiceTypeOrUserServicesId(String userId, String serviceType,
			String userServiceId);

	List<SpServiceDetails> findByUserIdAndServiceTypeAndUserServicesId(String userId, String serviceType,
			String userServiceId);

	@Query("SELECT s FROM SpServiceDetails s WHERE s.userId = :userId OR s.serviceType = :serviceType OR s.userServicesId = :userServiceId")
	List<SpServiceDetails> findByUserIdORServiceTypeORUserServiceId(String userId, String serviceType,
			String userServiceId);

	SpServiceDetails findByUserServicesId(String userServiceId);

//	AddServicesDto save(AddServicesDto dto);
	List<SpServiceDetails> findByServiceType(String serviceType);

	List<SpServiceDetails> findByServiceTypeOrLocation(String serviceType, String location);

	List<SpServiceDetails> findByServiceTypeAndLocation(String serviceType, String location);

	@Query("SELECT s FROM SpServiceDetails s WHERE s.userId = :userId AND s.status = 'Active'")
	List<SpServiceDetails> findByUserId(String userId);

	List<SpServiceDetails> findByLocation(String location);
	
	@Query("SELECT s FROM SpServiceDetails s WHERE " + "(:userId IS NULL OR s.userId = :userId) AND "
			+ "(:serviceTypes IS NULL OR s.serviceType IN :serviceTypes) AND "
			+ "(:serviceId IS NULL OR s.userServicesId = :serviceId)")
	List<SpServiceDetails> findByUserIdAndServiceTypesAndUserServiceId(String userId, List<String> serviceTypes,
			String serviceId);

}
