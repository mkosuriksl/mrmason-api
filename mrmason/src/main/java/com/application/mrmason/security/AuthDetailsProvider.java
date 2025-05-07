package com.application.mrmason.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class AuthDetailsProvider {
	public static String getLoggedEmail() {
		return JwtService.CURRENT_USER;
	}
	
	public static Collection<? extends GrantedAuthority> getLoggedRole() {
		return JwtService.CURRENT_ROLE;
	}
}
