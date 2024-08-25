package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.User;
import com.application.mrmason.enums.RegSource;

@Repository
public interface UserDAO extends JpaRepository<User, String> {

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	User findByEmail(String email);
	
	Optional<User> findByEmailAndRegSource(String email, RegSource regSource);

	User findByMobile(String mobile);

	User findByEmailOrMobile(String email, String mobile);

	@Query("SELECT u FROM User u WHERE (u.email = :email OR u.mobile = :mobile) AND u.regSource = :regSource")
	Optional<User> findByEmailOrMobileAndRegSource(String email, String mobile, RegSource regSource);

	@Query("SELECT u FROM User u WHERE (u.email = :email AND u.mobile = :mobile) OR u.email = :email OR u.mobile = :mobile")
	List<User> findByEmailANDMobile(String email, String mobile);

	User findByBodSeqNo(String bodSeqNo);

	User findByAddress(String address);

	List<User> findByEmailOrMobileOrStatusOrderByRegisteredDateDesc(String email, String mobile, String status);

	List<User> findByServiceCategory(String serviceCategory);

	@Query("SELECT cr FROM User cr WHERE cr.registeredDate BETWEEN :startDate AND :endDate")
	List<User> findByRegisteredDateBetween(String startDate, String endDate);

	List<User> findByState(String state);

	List<User> findByCity(String city);

//	List<User> findByPincodeNo(String pincodeNo);

	List<User> findByLocation(String location);

//	List<User> findByBodSeqNoIn(List<String> userIds);
	List<User> findByBodSeqNoIn(List<String> bodSeqNo);

}
