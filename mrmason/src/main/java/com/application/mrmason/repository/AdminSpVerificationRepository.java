package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminSpVerification;
import com.application.mrmason.entity.User;

public interface AdminSpVerificationRepository extends JpaRepository<AdminSpVerification, String> {

	 @Query("SELECT a FROM AdminSpVerification a WHERE a.bodSeqNo IN :bodSeqNos")
	    List<AdminSpVerification> findAllBodSeqNo(@Param("bodSeqNos") List<String> bodSeqNos);

	Optional<AdminSpVerification> findByBodSeqNo(String bodSeqNo);
}




