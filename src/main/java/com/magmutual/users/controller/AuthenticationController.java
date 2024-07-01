package com.magmutual.users.controller;

import com.magmutual.users.exception.CustomException;
import com.magmutual.users.model.JwtRequest;
import com.magmutual.users.model.JwtResponse;
import com.magmutual.users.service.AuthenticationService;
import com.magmutual.users.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param authenticationRequest the JWT request containing username and password
     * @return JwtResponse containing the JWT token
     * @throws CustomException if authentication fails or an error occurs
     */
    @Operation(summary = "Authenticate user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/authenticate")
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws CustomException {
        try {
            logger.info("Attempting to authenticate user: {}", authenticationRequest.getUsername());

            authenticateUser(authenticationRequest);

            final UserDetails userDetails = authenticationService.loadUserByUsername(authenticationRequest.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            logger.info("User {} authenticated successfully", authenticationRequest.getUsername());

            return new JwtResponse(jwt);
        } catch (BadCredentialsException e) {
            String errorDetails = "Invalid username or password";
            logger.error(errorDetails, e);
            throw new CustomException(errorDetails, "Invalid credentials provided", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            String errorDetails = "Authentication failed";
            logger.error(errorDetails, e);
            throw new CustomException("Authentication failed", errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticates the user.
     *
     * @param authenticationRequest the JWT request containing username and password
     * @throws BadCredentialsException if authentication fails
     */
    private void authenticateUser(JwtRequest authenticationRequest) throws BadCredentialsException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );
    }
}
