package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminSpVerification;
import com.application.mrmason.entity.MsServiceDetails;
import com.application.mrmason.entity.SpServiceWithNoOfProject;
import com.application.mrmason.entity.UploadUserProfileImage;

@Repository
public interface MsServiceDetailsRepo extends JpaRepository<MsServiceDetails, String> {
	List<MsServiceDetails> findByUserIdOrServiceTypeOrUserServicesId(String userId, String serviceType,
			String userServiceId);

	List<MsServiceDetails> findByUserIdAndServiceTypeAndUserServicesId(String userId, String serviceType,
			String userServiceId);

	@Query("SELECT s FROM MsServiceDetails s WHERE s.userId = :userId OR s.serviceType = :serviceType OR s.userServicesId = :userServiceId")
	List<MsServiceDetails> findByUserIdORServiceTypeORUserServiceId(String userId, String serviceType,
			String userServiceId);

	MsServiceDetails findByUserServicesId(String userServiceId);
	
	@Query("SELECT s FROM SpServiceWithNoOfProject s WHERE s.userServicesId = :userServicesId")
	List<SpServiceWithNoOfProject> findAllByUserServicesId(@Param("userServicesId") String userServicesId);
	
	@Query("SELECT s FROM UploadUserProfileImage s WHERE s.bodSeqNo = :bodSeqNo")
	List<UploadUserProfileImage> findAllBybodSeqNo(@Param("bodSeqNo") String bodSeqNo);


//	AddServicesDto save(AddServicesDto dto);
	List<MsServiceDetails> findByServiceType(String serviceType);

	List<MsServiceDetails> findByServiceTypeOrLocation(String serviceType, String location);

	List<MsServiceDetails> findByServiceTypeAndLocation(String serviceType, String location);

	@Query("SELECT s FROM MsServiceDetails s WHERE s.userId = :userId AND s.status = 'Active'")
	List<MsServiceDetails> findByUserId(String userId);

	List<MsServiceDetails> findByLocation(String location);
	
	@Query("SELECT s FROM MsServiceDetails s WHERE " + "(:userId IS NULL OR s.userId = :userId) AND "
			+ "(:serviceTypes IS NULL OR s.serviceType IN :serviceTypes) AND "
			+ "(:serviceId IS NULL OR s.userServicesId = :serviceId)")
	List<MsServiceDetails> findByUserIdAndServiceTypesAndUserServiceId(String userId, List<String> serviceTypes,
			String serviceId);

	List<MsServiceDetails> findByUserIdAndStatus(String bodSeqNo, String string);
	
	@Query("SELECT u FROM UploadUserProfileImage u WHERE u.bodSeqNo IN :bodSeqNos")
	List<UploadUserProfileImage> findAllByBodSeqNoIn(@Param("bodSeqNos") List<String> bodSeqNos);

	@Query("SELECT u FROM AdminSpVerification u WHERE u.bodSeqNo IN :bodSeqNos")
	List<AdminSpVerification> findAllBodSeqNo(@Param("bodSeqNos") List<String> bodSeqNos);

	@Query("SELECT u FROM SpServiceWithNoOfProject u WHERE u.userServicesId IN :userServicesId")
	List<SpServiceWithNoOfProject> findAllByUserServicesId(List<String> userServicesId);

}
