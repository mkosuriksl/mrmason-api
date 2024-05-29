package com.application.mrmason.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.application.mrmason.entity.AdminMail;
import com.application.mrmason.repository.AdminMailRepo;

import lombok.Getter;

@Getter
@Configuration
public class AppProperties {
//	@Autowired
//	AdminMailRepo mailRepo;
//	
//	AdminMail admin=mailRepo.findByEmailid("Mrmason.in@kosuriers.com");
	
    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean mailSmtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean mailSmtpStartTlsEnable;

    @Value("${spring.web.resources.static-locations}")
    private String staticLocations;

    @Value("${mrmason.textLocal.api-key}")
    private String apiKey;

    @Value("${mrmason.textLocal.sender}")
    private String sender;

    @Value("${mrmason.textLocal.url}")
    private String url;


}
