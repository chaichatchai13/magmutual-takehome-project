package com.magmutual.users.filter;


import com.magmutual.users.exception.CustomException;
import com.magmutual.users.model.ErrorResponse;
import com.magmutual.users.service.AuthenticationService;
import com.magmutual.users.utils.JwtUtil;
import com.magmutual.users.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Filters each request and checks for JWT token in the Authorization header.
     * If a valid token is found, it sets the authentication in the SecurityContext.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @param chain    the FilterChain
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.authenticationService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) jwtUtil.extractAllClaims(jwt)
                            .get("authorities", List.class).stream()
                            .map(authority -> new SimpleGrantedAuthority((String) authority))
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            logger.error("Token has expired", ex);
            ResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has expired", "The provided token has expired.");
        } catch (SignatureException | MalformedJwtException e) {
            logger.error("Token is invalid", e);
            ResponseUtil.writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token is invalid", "The provided token is invalid.");
        }
    }
}


