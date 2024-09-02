package com.application.mrmason.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.application.mrmason.service.impl.CustomUserService;

@Configuration
public class WebConfig {

	@Autowired
	CustomUserService registrationService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public WebConfig(BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Bean
	public DaoAuthenticationProvider customDaoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(registrationService);
		provider.setPasswordEncoder(bCryptPasswordEncoder);
		return provider;
	}

	@Bean
	public JwtAuthenticationFilter authenticationJwtTokenFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling((exception) -> exception.authenticationEntryPoint(new JwtAuthEntryPoint()))
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/error", "/addAdminDetails",
						"/adminLoginWithPass", "/addNewUser", "/sendOtp", "/verifyOtp", "/sendSmsOtp", "/verifySmsOtp",
						"/sp-register", "/sp-login", "/sp-send-email-otp", "/sp-verify-email-otp",
						"/sp-send-mobile-otp", "/sp-verify-mobile-otp", "/forgetPassword/sendOtp",
						"/forgetPassword/verifyOtpAndChangePassword", "/forget-pwd-send-otp", "/forget-pwd-change",
						"/admin/forgetPassword/sendOtp", "/admin/forgetPassword/verifyOtpAndChangePassword", "/getData",
						"/getServiceCategory/civil/{serviceCategory}", "/getAssetCategory/civil/{assetCategory}",
						"/getServiceCategory/nonCivil/{serviceCategory}", "/getServiceRequest",
						"/getAssetCategory/nonCivil/{assetCategory}", "/getAdminAsset/civil/{assetCat}",
						"/getAdminAsset/nonCivil/{assetCat}", "/filterServicePerson", "/getServicePersonDetails",
						"/paint-master/**").permitAll().anyRequest().authenticated());

		http.authenticationProvider(customDaoAuthenticationProvider());
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Arrays.asList("*")); // Adjust this to specific origins as needed
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
	    configuration.setAllowedHeaders(Arrays.asList("*"));
	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
}
