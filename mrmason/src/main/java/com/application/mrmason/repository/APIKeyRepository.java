package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.application.mrmason.entity.APIKEY;

@Repository
public interface APIKeyRepository extends JpaRepository<APIKEY, String> {
}

