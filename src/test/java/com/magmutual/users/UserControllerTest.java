package com.magmutual.users;

import com.magmutual.users.controller.UserController;
import com.magmutual.users.entity.Users;
import com.magmutual.users.model.UserRequest;
import com.magmutual.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;

import static com.magmutual.users.utils.DateUtil.convertStringToDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private Logger logger;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private Users user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new Users();
        user.setId(1L);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setProfession("Developer");
        user.setDateCreated(convertStringToDate("2023-01-01"));
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
    void testGetUserById() throws Exception {
        when(userService.getUserById(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.addUser(any(UserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": 1, \"firstname\": \"John\", \"lastname\": \"Doe\", \"email\": \"john.doe@example.com\", \"profession\": \"Developer\", \"dateCreated\": \"2023-01-01\", \"country\": \"USA\", \"city\": \"New York\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(anyString(), any(UserRequest.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": 1, \"firstname\": \"John\", \"lastname\": \"Doe\", \"email\": \"john.doe@example.com\", \"profession\": \"Developer\", \"dateCreated\": \"2023-01-01\", \"country\": \"USA\", \"city\": \"New York\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", "1"))
                .andExpect(status().isNoContent());
    }
}
