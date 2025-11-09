package com.application.mrmason.security;

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
		http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling((exception) -> exception.authenticationEntryPoint(new JwtAuthEntryPoint()))
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/error", "/addAdminDetails",
						"/adminLoginWithPass", "/addNewUser", "/sendOtp", "/verifyOtp", "/sendSmsOtp", "/verifySmsOtp",
						"/sp-register", "/sp-login", "/sp-send-email-otp", "/sp-verify-email-otp","/ms-send-mobile-otp","/ms-verify-mobile-otp","/ms-login",
						"/ms-forget-pwd-send-otp","/ms-forget-pwd-change","/material-supplier-quotation-register","/ms-send-email-otp","/ms-verify-email-otp",
						"/sp-send-mobile-otp", "/sp-verify-mobile-otp", "/forgetPassword/sendOtp",
						"/forgetPassword/verifyOtpAndChangePassword", "/forget-pwd-send-otp", "/forget-pwd-change",
						"/admin/forgetPassword/sendOtp", "/admin/forgetPassword/verifyOtpAndChangePassword", "/getData",
						"/getServiceCategory/civil/{serviceCategory}", "/getAssetCategory/civil/{assetCategory}",
						"/getServiceCategory/nonCivil/{serviceCategory}", "/getServiceCategory", "/getServiceRequest",
						"/admin-material-master/get-brand-by-materialcategory","/admin-material-master/distinct-material-category",
						"/getAssetCategory/nonCivil/{assetCategory}", "/getAdminAsset/civil/{assetCat}",
						"/getAdminAsset/nonCivil/{assetCat}", "/filterServicePerson", "/getServicePersonDetails",
						"/paint-master/**", "/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**",
						"/api/fr/register","/api/fr/send-otp","/api/fr/verify-otp",
						"/api/fr/login","/api/fr/forgot/verify-otp","/api/fr/forgot/send-otp",
						"/api/distinct-location-by-machine",
						"/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security","/api/distinct-location-by-ms","/distinct-location-by-sp",
						"/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/getRentalAssetsNoAuth","/getAdminUiEndPoint","/api/home-search-by-location","/api/home-search-by-machine").permitAll()
						.anyRequest().authenticated());

		http.authenticationProvider(customDaoAuthenticationProvider());
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}
