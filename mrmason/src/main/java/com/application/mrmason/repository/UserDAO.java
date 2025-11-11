package com.application.mrmason.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;

@Repository
public interface UserDAO extends JpaRepository<User, String> {

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	User findByEmail(String email);
	
	@Query("SELECT s FROM User s WHERE s.email = :email")
	Optional<User> findByEmailOne(@Param("email") String email);
	
	@Query("SELECT s FROM User s WHERE s.email = :email AND s.userType = :userType AND s.regSource = :regSource")
	Optional<User> findByEmailAndUserTypeAndRegSource(@Param("email") String email,@Param("userType") UserType userType,RegSource regSource);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.regSource = :regSource")
	Optional<User> findByEmailAndRegSource(String email, RegSource regSource);

	@Query("SELECT u FROM User u WHERE u.mobile = :mobile  AND u.regSource = :regSource")
	Optional<User> findByMobileAndRegSource(String mobile, RegSource regSource);

	User findByMobile(String mobile);

	User findByEmailOrMobile(String email, String mobile);

	@Query("SELECT u FROM User u WHERE (u.email = :email OR u.mobile = :mobile) AND u.regSource = :regSource")
	Optional<User> findByEmailOrMobileAndRegSource(String email, String mobile, RegSource regSource);

	@Query("SELECT u FROM User u WHERE (u.email = :email AND u.mobile = :mobile) OR u.email = :email OR u.mobile = :mobile")
	List<User> findByEmailANDMobile(String email, String mobile);

	User findByBodSeqNo(String bodSeqNo);

	@Query("SELECT c FROM User c WHERE c.bodSeqNo = :bodSeqNo")
	Optional<User> findByBodSeqNos(String bodSeqNo);
	
	User findByAddress(String address);

	List<User> findByEmailOrMobileOrStatusOrderByRegisteredDateDesc(String email, String mobile, String status);

	List<User> findByServiceCategory(String serviceCategory);

	@Query("SELECT cr FROM User cr WHERE cr.registeredDate BETWEEN :startDate AND :endDate")
	List<User> findByRegisteredDateBetween(String startDate, String endDate);

	List<User> findByState(String state);

	List<User> findByCity(String city);

	// List<User> findByPincodeNo(String pincodeNo);

	List<User> findByLocation(String location);

	// List<User> findByBodSeqNoIn(List<String> userIds);
	List<User> findByBodSeqNoIn(List<String> bodSeqNo);
	
	@Query("SELECT u FROM User u WHERE (u.mobile = :contact OR u.email = :contact) AND u.bodSeqNo = :userId")
	Optional<User> findByMobileOrEmailAndBodSeqNo(String contact, String userId);

	@Query("SELECT s FROM User s WHERE s.email = :email AND s.userType = :userType")
	Optional<User> findByEmailAndUserType(@Param("email") String email, @Param("userType") UserType userType);

	@Query("SELECT u FROM User u WHERE u.bodSeqNo = :bodSeqNo")
	Optional<User> findByBodSeqNoUploadImage(@Param("bodSeqNo") String bodSeqNo);

}
