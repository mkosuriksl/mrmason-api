package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.UserType;
@Repository
public interface CustomerRegistrationRepo extends JpaRepository<CustomerRegistration, Long>{
	CustomerRegistration findByUserEmailOrUserMobile(String email,String phNo);
	List<CustomerRegistration> findAllByUserEmailOrUserMobileOrUserState(String email,String phNo,String userState);
	CustomerRegistration findByUserEmail(String email);
	CustomerRegistration findByUserid(String userid);
	
	@Query("SELECT cr FROM CustomerRegistration cr WHERE cr.regDate BETWEEN :startDate AND :endDate")
	List<CustomerRegistration> findByRegDateBetween(String startDate, String endDate);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userEmail = :userEmail")
    CustomerRegistration findByUserEmailCustomQuery(@Param("userEmail") String userEmail);
	CustomerRegistration findByUserMobile(String mobile);
	Optional<CustomerRegistration> findByUserEmailAndUserType(String loggedInUserEmail, UserType userType);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userid IN :userIds")
	List<CustomerRegistration> findByUserIds(@Param("userIds") List<String> userIds);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userid = :userid")
	Optional<CustomerRegistration> findByUserids(String userid);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userEmail = :userEmail")
	Optional<CustomerRegistration> findByUserEmailOne(String userEmail);
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userMobile = :userMobile")
    Optional<CustomerRegistration> findByUserMobileOne(String userMobile);


}
