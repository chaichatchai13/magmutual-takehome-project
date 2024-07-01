package com.magmutual.users;

import com.magmutual.users.entity.Users;
import com.magmutual.users.model.UserRequest;
import com.magmutual.users.repository.UserRepository;
import com.magmutual.users.service.UserService;
import com.magmutual.users.utils.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Users user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1L);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setProfession("Developer");
        user.setDateCreated(DateUtil.convertStringToDate("2023-01-01"));
        user.setCountry("USA");
        user.setCity("New York");

        userRequest = new UserRequest();
        userRequest.setId(123);
        userRequest.setFirstname("John");
        userRequest.setLastname("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setProfession("Developer");
        userRequest.setDateCreated("2023-01-01");
        userRequest.setCountry("USA");
        userRequest.setCity("New York");
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        Optional<Users> result = userService.getUserById("1");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testUpdateUser() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users updatedUser = userService.updateUser("1", userRequest);
        assertEquals(user, updatedUser);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(anyString());

        userService.deleteUser("1");
        verify(userRepository, times(1)).deleteById("1");
    }

    @Test
    void testGetUsers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Users> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<Users> result = userService.getUsers(0, 10, "id", "asc", null, null, null);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testAddUser() {
        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users savedUser = userService.addUser(userRequest);
        assertEquals(user, savedUser);
    }
}
