package com.application.mrmason.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

    // Load the encrypted values and other necessary configurations from application.properties
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;

    @Value("${spring.datasource.username}")
    private String encryptedUsername;

    @Value("${spring.datasource.password}")
    private String encryptedPassword;

    @Bean
    public DriverManagerDataSource dataSource() throws Exception {

        String decryptedUsername = EncryptCredentials.decrypt(encryptedUsername);
        String decryptedPassword = EncryptCredentials.decrypt(encryptedPassword);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(datasourceUrl);
        dataSource.setUsername(decryptedUsername);
        dataSource.setPassword(decryptedPassword);
        return dataSource;
    }
}
