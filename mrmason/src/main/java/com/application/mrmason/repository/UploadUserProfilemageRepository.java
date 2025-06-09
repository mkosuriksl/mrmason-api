package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.UploadUserProfileImage;
import com.application.mrmason.entity.User;

@Repository
public interface UploadUserProfilemageRepository extends JpaRepository<UploadUserProfileImage, String> {

	Optional<UploadUserProfileImage> findByBodSeqNo(String bodSeqNo);
}

