package com.application.mrmason.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.FrLogin;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.User;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.FrLoginRepo;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.UserDAO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private AdminDetailsRepo adminDetailsRepo;
	
	@Autowired
	private CustomerRegistrationRepo customerRegistrationRepo;
	
	@Autowired
	private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;
	
	@Autowired
	private FrRegRepository frRegRepository;

	private static final String ORIGIN = "Origin";

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");

		String origin = request.getHeader(ORIGIN);
		if (origin != null) {
			response.setHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
			response.setHeader("Access-Control-Allow-Methods", "PATCH, GET, POST, PUT, DELETE, OPTIONS");
		}

		if (request.getMethod().equals("OPTIONS")) {
			// stop here if OPTIONS used
			try {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/plain");
				response.getWriter().print("OK");
				response.getWriter().flush();
			} catch (IOException e) {
			}
		} else {

			if (authHeader != null && authHeader.startsWith("Bearer ") && !request.getRequestURI().equals("/error")) {
				String token = authHeader.substring(7);
				try {
					String username = jwtService.extractUsername(token);
					String userId = jwtService.extractUserId(token);
					String userType = jwtService.extractUserType(token);
					RegSource regSource=jwtService.extractRegSource(token);
					UserDetails userDetails = null;
					if (userType.equals("Developer") || userType.equals("worker")) {
						User user = userDAO.findByMobileOrEmailAndBodSeqNo(username, userId).get();
						userDetails = user;
					}

					if (userType.equals("Adm")) {
						userDetails = adminDetailsRepo.findByEmail(username);
					}

					if (userType.equals("EC")) {
						CustomerRegistration customer = customerRegistrationRepo.findByUserMobileOrUserEmailAndUserid(username,userId).get();
						userDetails=customer;
					}
					if (userType.equals("MS")) {
						MaterialSupplierQuotationUser material = materialSupplierQuotationUserDAO.findByMobileOrEmailAndBodSeqNo(username,userId).get();
						userDetails=material;
					}
					if (userType.equals("FR")) {
						userDetails = frRegRepository.findByFrEmails(username);
					}

					if (!jwtService.isTokenValid(token, userDetails)) {
						response.sendRedirect("/error");
						return;
					}

					// If the token is valid, set up Spring Security context
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} catch (Exception e) {
					response.sendRedirect("/error");
					return;
				}

			}
			if (!response.isCommitted()) {
				filterChain.doFilter(request, response);
			}
		}
	}

}
