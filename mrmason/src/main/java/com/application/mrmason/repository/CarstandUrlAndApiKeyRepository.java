package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CarstandUrlAndApiKeyEntity;


@EnableJpaRepositories
@Repository
public interface CarstandUrlAndApiKeyRepository extends JpaRepository<CarstandUrlAndApiKeyEntity, Long>{

}
