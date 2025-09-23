package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.MessageTemplate;

import java.util.Optional;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {
    Optional<MessageTemplate> findByTemplateCode(String templateCode);
}
