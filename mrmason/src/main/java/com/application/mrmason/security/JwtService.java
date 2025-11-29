package com.application.mrmason.security;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${application.security.jwt.secret-key}")
	private String secretKey;
	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration;
	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration;
	
	public static String CURRENT_USER = "";

	public static Collection<? extends GrantedAuthority> CURRENT_ROLE =  Collections.emptyList();;
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractUserId(String token) {
		return extractClaim(token, claims -> claims.get("userId", String.class));
	}
	
	public String extractUserType(String token) {
		return extractClaim(token, claims -> claims.get("userType", String.class));
	}
	
	public RegSource extractRegSource(String token) {
	    String regSourceStr = extractClaim(token, claims -> claims.get("regSource", String.class));
	    if (regSourceStr == null) {
	        return null; // or throw exception if regSource must always exist
	    }
	    return RegSource.valueOf(regSourceStr.toUpperCase()); 
	}


	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String generateToken(UserDetails userDetails,String userId) {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("userType", getUserTypeFromUserDetails(userDetails));
		return buildToken(extraClaims, userDetails,userId, jwtExpiration, "your-issuer", "your-audience");
	}

	public String generateRefreshToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails, null,refreshExpiration, "your-issuer", "your-audience");
	}

	
	private Object getUserTypeFromUserDetails(UserDetails userDetails) {
		if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Developer"))) {
			return UserType.Developer;
		} else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EC"))) {
			return UserType.EC;
		} else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Adm"))) {
			return UserType.Adm;
		}else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MS"))) {
			return UserType.MS;
		}else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FR"))) {
			return UserType.FR;
		}else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_RT"))) {
			return UserType.RT;
		}
		
		return null;
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, String userId, long expiration, String issuer,
			String audience) {
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).claim("userId", userId)
				.setIssuer(issuer).setAudience(audience).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		CURRENT_USER = userDetails.getUsername();
		CURRENT_ROLE=userDetails.getAuthorities();
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}
	

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public UserDetails getUserDetails(User registration) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + registration.getUserType()));

		return new org.springframework.security.core.userdetails.User(registration.getEmail(),
				registration.getPassword(), authorities);
	}

	public UserDetails getUserDetails(CustomerRegistration registration) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + registration.getUserType()));

		return new org.springframework.security.core.userdetails.User(registration.getUserEmail(),
				registration.getPassword(), authorities);
	}
	
	public UserDetails getUserDetails(MaterialSupplierQuotationUser registration) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + registration.getUserType()));

		return new org.springframework.security.core.userdetails.User(registration.getEmail(),
				registration.getPassword(), authorities);
	}
	
	public UserDetails getUserDetails(FrReg registration) {
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + registration.getUserType()));

		return new org.springframework.security.core.userdetails.User(registration.getFrEmail(),
				registration.getPassword(), authorities);
	}

}
