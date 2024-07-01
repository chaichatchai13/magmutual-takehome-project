package com.magmutual.users.config;

import com.magmutual.users.exception.CustomException;
import com.magmutual.users.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Value("${cors.allowed.origin}")
    private String corsAllowedOrigin;

    /**
     * Configures the CSRF token repository to use cookies with HttpOnly set to false.
     *
     * @return the CSRF token repository
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity to configure
     * @return the SecurityFilterChain
     * @throws CustomException if an error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws CustomException {
        try {
            logger.info("Configuring security filter chain");

            CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
            requestHandler.setCsrfRequestAttributeName(null);

            http.csrf(csrf -> csrf
                            .csrfTokenRepository(csrfTokenRepository())
                            .csrfTokenRequestHandler(requestHandler)
                            .ignoringRequestMatchers("/api/auth/authenticate")
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/authenticate").permitAll()
                            .requestMatchers("/api/users/**").authenticated()
                            .anyRequest().permitAll()
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

            logger.info("Security filter chain configured successfully");

            return http.build();
        } catch (Exception e) {
            String errorDetails = "Error configuring security filter chain";
            logger.error(errorDetails, e);
            throw new CustomException("Security configuration failed", errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Configures the password encoder to use BCrypt.
     *
     * @return the PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication manager.
     *
     * @param authenticationConfiguration the AuthenticationConfiguration to use
     * @return the AuthenticationManager
     * @throws CustomException if an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws CustomException {
        try {
            return authenticationConfiguration.getAuthenticationManager();
        } catch (Exception e) {
            String errorDetails = "Error configuring authentication manager";
            logger.error(errorDetails, e);
            throw new CustomException("Authentication manager configuration failed", errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Configures the CORS filter.
     *
     * @return the CorsFilter
     * @throws CustomException if an error occurs
     */
    @Bean
    public CorsFilter corsFilter() throws CustomException {
        try {
            logger.info("Configuring CORS filter with allowed origin: {}", corsAllowedOrigin);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOrigin(corsAllowedOrigin);
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            source.registerCorsConfiguration("/**", config);

            logger.info("CORS filter configured successfully");

            return new CorsFilter(source);
        } catch (Exception e) {
            String errorDetails = "Error configuring CORS filter";
            logger.error(errorDetails, e);
            throw new CustomException("CORS configuration failed", errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
