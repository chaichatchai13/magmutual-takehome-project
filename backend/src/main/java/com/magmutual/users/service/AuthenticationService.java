package com.magmutual.users.service;

import com.magmutual.users.constants.ApplicationConstants;
import com.magmutual.users.constants.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Value("${app.user.password}")
    private String userPassword;

    @Value("${app.admin.password}")
    private String adminPassword;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Loads the user by username and returns UserDetails object.
     *
     * @param username the username of the user
     * @return UserDetails object
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (UserRole.USER.getRole().equals(username)) {
            return createUserDetails(username, userPassword, List.of(new SimpleGrantedAuthority(ApplicationConstants.GET_USERS)));
        } else if (UserRole.ADMIN.getRole().equals(username)) {
            return createUserDetails(username, adminPassword, List.of(
                    new SimpleGrantedAuthority(ApplicationConstants.GET_USERS),
                    new SimpleGrantedAuthority(ApplicationConstants.POST_USERS),
                    new SimpleGrantedAuthority(ApplicationConstants.PUT_USERS),
                    new SimpleGrantedAuthority(ApplicationConstants.DELETE_USERS)
            ));
        } else {
            String errorMessage = "User not found with username: " + username;
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }
    }

    /**
     * Creates a UserDetails object with encoded password and authorities.
     *
     * @param username   the username of the user
     * @param password   the plain text password of the user
     * @param authorities the authorities granted to the user
     * @return UserDetails object
     */
    private UserDetails createUserDetails(String username, String password, List<GrantedAuthority> authorities) {
        String encodedPassword = passwordEncoder.encode(password);
        return new User(username, encodedPassword, authorities);
    }
}
