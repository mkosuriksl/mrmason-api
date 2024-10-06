package com.application.mrmason.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class AppProperties {
	
    @Value("${mrmason.textLocal.api-key}")
    private String apiKey;

    @Value("${mrmason.textLocal.sender}")
    private String sender;

    @Value("${mrmason.textLocal.url}")
    private String url;


}
