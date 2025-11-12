package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;

@Repository
public interface MaterialSupplierQuotationUserDAO extends JpaRepository<MaterialSupplierQuotationUser, String> {

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	MaterialSupplierQuotationUser findByEmail(String email);
	
	@Query("SELECT s FROM MaterialSupplierQuotationUser s WHERE s.email = :email")
	Optional<MaterialSupplierQuotationUser> findByEmailOne(@Param("email") String email);
	
	@Query("SELECT s FROM MaterialSupplierQuotationUser s WHERE s.email = :email AND s.userType = :userType AND s.regSource = :regSource")
	Optional<MaterialSupplierQuotationUser> findByEmailAndUserTypeAndRegSource(@Param("email") String email,@Param("userType") UserType userType,RegSource regSource);

	@Query("SELECT u FROM MaterialSupplierQuotationUser u WHERE u.email = :email AND u.regSource = :regSource")
	Optional<MaterialSupplierQuotationUser> findByEmailAndRegSource(String email, RegSource regSource);

	@Query("SELECT u FROM MaterialSupplierQuotationUser u WHERE u.mobile = :mobile  AND u.regSource = :regSource")
	Optional<MaterialSupplierQuotationUser> findByMobileAndRegSource(String mobile, RegSource regSource);

	MaterialSupplierQuotationUser findByMobile(String mobile);

	MaterialSupplierQuotationUser findByEmailOrMobile(String email, String mobile);

	@Query("SELECT u FROM MaterialSupplierQuotationUser u WHERE (u.email = :email OR u.mobile = :mobile) AND u.regSource = :regSource")
	Optional<MaterialSupplierQuotationUser> findByEmailOrMobileAndRegSource(String email, String mobile, RegSource regSource);

	@Query("SELECT u FROM MaterialSupplierQuotationUser u WHERE (u.email = :email AND u.mobile = :mobile) OR u.email = :email OR u.mobile = :mobile")
	List<MaterialSupplierQuotationUser> findByEmailANDMobile(String email, String mobile);

	MaterialSupplierQuotationUser findByBodSeqNo(String bodSeqNo);

	MaterialSupplierQuotationUser findByAddress(String address);

	List<MaterialSupplierQuotationUser> findByEmailOrMobileOrStatusOrderByRegisteredDateDesc(String email, String mobile, String status);

	List<MaterialSupplierQuotationUser> findByServiceCategory(String serviceCategory);

	@Query("SELECT cr FROM MaterialSupplierQuotationUser cr WHERE cr.registeredDate BETWEEN :startDate AND :endDate")
	List<MaterialSupplierQuotationUser> findByRegisteredDateBetween(String startDate, String endDate);

	List<MaterialSupplierQuotationUser> findByState(String state);

	List<MaterialSupplierQuotationUser> findByCity(String city);


	List<MaterialSupplierQuotationUser> findByLocation(String location);

	List<MaterialSupplierQuotationUser> findByBodSeqNoIn(List<String> bodSeqNo);
	
	@Query("SELECT u FROM MaterialSupplierQuotationUser u WHERE (u.mobile = :contact OR u.email = :contact) AND u.bodSeqNo = :userId")
	Optional<MaterialSupplierQuotationUser> findByMobileOrEmailAndBodSeqNo(String contact, String userId);

	@Query("SELECT s FROM MaterialSupplierQuotationUser s WHERE s.email = :email AND s.userType = :userType")
	Optional<MaterialSupplierQuotationUser> findByEmailAndUserType(@Param("email") String email, @Param("userType") UserType userType);

	@Query("SELECT u FROM MaterialSupplierQuotationUser u WHERE u.bodSeqNo = :bodSeqNo")
	Optional<MaterialSupplierQuotationUser> findByBodSeqNoUploadImage(@Param("bodSeqNo") String bodSeqNo);

	List<MaterialSupplierQuotationUser> findByLocationContaining(String location);

//	List<MaterialSupplierQuotationUser> findByLocationIgnoreCase(String location);
	@Query("SELECT m FROM MaterialSupplierQuotationUser m " +
		       "WHERE LOWER(m.location) LIKE LOWER(CONCAT(:prefix, '%')) ESCAPE '\\'")
		List<MaterialSupplierQuotationUser> searchByLocationPrefix(@Param("prefix") String prefix);
	
	@Query("SELECT DISTINCT m.location FROM MaterialSupplierQuotationUser m " +
		       "WHERE LOWER(m.location) LIKE LOWER(CONCAT(:prefix, '%')) " +
		       "AND m.location IS NOT NULL")
		List<String> findDistinctLocationsByPrefix(@Param("prefix") String prefix);

	@Query("SELECT DISTINCT m.location FROM MaterialSupplierQuotationUser m WHERE m.bodSeqNo IN :supplierIds AND LOWER(m.location) LIKE LOWER(CONCAT(:safeInput, '%'))")
	List<String> findDistinctByBodSeqNoInAndLocationStartingWithIgnoreCase(
	        @Param("supplierIds") List<String> supplierIds,
	        @Param("safeInput") String safeInput);



}
