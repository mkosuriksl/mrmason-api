package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminMsVerification;

public interface AdminMSVerificationRepository extends JpaRepository<AdminMsVerification, String> {

	 @Query("SELECT a FROM AdminMsVerification a WHERE a.bodSeqNo IN :bodSeqNos")
	    List<AdminMsVerification> findAllBodSeqNo(@Param("bodSeqNos") List<String> bodSeqNos);

	Optional<AdminMsVerification> findByBodSeqNo(String bodSeqNo);
}




