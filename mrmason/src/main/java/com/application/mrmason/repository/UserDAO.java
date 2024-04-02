package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.User;

@Repository
public interface UserDAO extends JpaRepository<User, String> {

	boolean existsByEmail(String email);

	boolean existsByMobile(String mobile);

	User findByEmail(String email);

	User findByEmailOrMobile(String email, String mobile);

	User findByBodSeqNo(String bodSeqNo);

	User findByAddress(String address);

}
