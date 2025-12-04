package com.application.mrmason.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvnWebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:80","http://3.109.225.143").allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowedHeaders("*");
	}

}
