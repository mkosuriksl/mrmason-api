package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.MaterialSupplierQuotationLogin;
import com.application.mrmason.enums.RegSource;

@Repository
public interface MaterialSupplierQuotatuionLoginDAO extends JpaRepository<MaterialSupplierQuotationLogin, Long> {

	MaterialSupplierQuotationLogin findByEmail(String email);

	@Query("SELECT u FROM MaterialSupplierQuotationLogin u WHERE LOWER(u.email) = LOWER(:email)")
	List<MaterialSupplierQuotationLogin> findByEmailOne(@Param("email") String email);

	Optional<MaterialSupplierQuotationLogin> findByEmailAndRegSource(String email, RegSource regSource);

	@Query("SELECT s FROM MaterialSupplierQuotationLogin s WHERE (s.email = :email OR s.mobile = :mobile) AND s.regSource = :regSource")
	Optional<MaterialSupplierQuotationLogin> findByEmailOrMobileAndRegSource(String email, String mobile, RegSource regSource);

	@Query("SELECT s FROM MaterialSupplierQuotationLogin s WHERE (s.email = :contact OR s.mobile = :contact) AND s.regSource = :regSource")
	MaterialSupplierQuotationLogin findByEmailOrMobileAndRegSource(String contact, RegSource regSource);

	MaterialSupplierQuotationLogin findByMobile(String mobile);

	Optional<MaterialSupplierQuotationLogin> findByMobileAndRegSource(String mobile, RegSource regSource);
	
	@Query("SELECT m FROM MaterialSupplierQuotationLogin m WHERE m.mobile = :mobile")
	Optional<MaterialSupplierQuotationLogin> findByMobil(@Param("mobile") String mobile);

	
	@Query("SELECT s FROM MaterialSupplierQuotationLogin s WHERE LOWER(s.email) = LOWER(:email) AND s.eOtp = :eOtp AND s.regSource = :regSource")
	MaterialSupplierQuotationLogin findByEmailEOtpAndRegSourceIgnoreCase(@Param("email") String email, @Param("eOtp") String eOtp, @Param("regSource") RegSource regSource);

	@Query("SELECT s FROM MaterialSupplierQuotationLogin s WHERE LOWER(s.mobile) = LOWER(:mobile) AND s.mOtp = :mOtp AND s.regSource = :regSource")
	MaterialSupplierQuotationLogin findByMobileMOtpAndRegSourceIgnoreCase(@Param("mobile") String mobile, @Param("mOtp") String mOtp, @Param("regSource") RegSource regSource);

}
