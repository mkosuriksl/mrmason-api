package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
@Repository
public interface CustomerRegistrationRepo extends JpaRepository<CustomerRegistration, Long>{
	CustomerRegistration findByUserEmailOrUserMobile(String email,String phNo);
	@Query("SELECT u FROM CustomerRegistration u WHERE " +
		       "(u.userEmail = :email OR u.userMobile = :mobile) " +
		       "AND u.regSource = :regSource")
		CustomerRegistration findUserWithSameRegSource(@Param("email") String email, 
		                                               @Param("mobile") String mobile, 
		                                               @Param("regSource") RegSource regSource);

	List<CustomerRegistration> findAllByUserEmailOrUserMobileOrUserState(String email,String phNo,String userState);
	CustomerRegistration findByUserEmailAndRegSource(String email,RegSource regSource);
	CustomerRegistration findByUserid(String userid);
	
	@Query("SELECT cr FROM CustomerRegistration cr WHERE cr.regDate BETWEEN :startDate AND :endDate")
	List<CustomerRegistration> findByRegDateBetween(String startDate, String endDate);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userEmail = :userEmail")
    CustomerRegistration findByUserEmailCustomQuery(@Param("userEmail") String userEmail);
	CustomerRegistration findByUserMobileAndRegSource(String mobile,RegSource regSource);
	Optional<CustomerRegistration> findByUserEmailAndUserType(String loggedInUserEmail, UserType userType);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userid IN :userIds")
	List<CustomerRegistration> findByUserIds(@Param("userIds") List<String> userIds);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userid = :userid")
	Optional<CustomerRegistration> findByUserids(String userid);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userEmail = :userEmail")
	Optional<CustomerRegistration> findByUserEmailOne(String userEmail);
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userMobile = :userMobile")
    Optional<CustomerRegistration> findByUserMobileOne(String userMobile);
	List<CustomerRegistration> findByUserTown(String trim);
	
	@Query("SELECT cr FROM CustomerRegistration cr WHERE cr.regDate BETWEEN :startDate AND :endDate")
	List<CustomerRegistration> findByRegisteredDateBetween(String startDate, String endDate);
	
	@Query("SELECT c FROM CustomerRegistration c WHERE c.userEmail = :userEmail OR c.userMobile = :userMobile")
	List<CustomerRegistration> findByUserEmailOrMobileNumberCustom(@Param("userEmail") String userEmail, @Param("userMobile") String userMobile);
	List<CustomerRegistration> findByUserPincode(String userPincode);
	
	@Query("SELECT s FROM CustomerRegistration s WHERE s.userEmail = :userEmail AND s.userType = :userType AND s.regSource = :regSource")
	Optional<CustomerRegistration> findByUserEmailAndUserTypeAndRegSource(@Param("userEmail") String userEmail,@Param("userType") UserType userType,RegSource regSource);
	
	List<CustomerRegistration> findByUserMobileOrUserEmail(String customerNumber, String customerEmail);


}
