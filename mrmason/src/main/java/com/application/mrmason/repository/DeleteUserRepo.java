package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.DeleteUser;

@Repository
public interface DeleteUserRepo extends JpaRepository<DeleteUser, Long> {

	

}
