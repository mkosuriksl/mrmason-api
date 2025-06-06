package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.UploadUserProfileImage;

@Repository
public interface UploadUserProfilemageRepository extends JpaRepository<UploadUserProfileImage, String> {
}

