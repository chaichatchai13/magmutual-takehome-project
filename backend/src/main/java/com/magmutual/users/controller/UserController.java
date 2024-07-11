package com.magmutual.users.controller;

import com.magmutual.users.entity.Users;
import com.magmutual.users.exception.CustomException;
import com.magmutual.users.model.UserRequest;
import com.magmutual.users.service.UserService;
import com.magmutual.users.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@RestController
@RequestMapping("api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Retrieves a paginated list of users with optional filters for date range and profession.
     *
     * @param offset        the starting point of the result set
     * @param limit         the maximum number of results to return
     * @param sortBy        the field to sort by
     * @param sortDirection the direction to sort (asc or desc)
     * @param startDateStr  the start date for filtering users
     * @param endDateStr    the end date for filtering users
     * @param profession    the profession for filtering users
     * @return a paginated list of users
     */
    @Operation(summary = "Retrieve a paginated list of users with optional filters for date range and profession")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('" + ApplicationConstants.GET_USERS + "')")
    public ResponseEntity<Page<Users>> getUsers(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "profession", required = false) String profession) {

        try {
            Date startDate = startDateStr != null ? Date.valueOf(startDateStr) : null;
            Date endDate = endDateStr != null ? Date.valueOf(endDateStr) : null;

            if (startDate != null && endDate != null && endDate.before(startDate)) {
                throw new CustomException("Invalid date range", "endDate cannot be before startDate", HttpStatus.BAD_REQUEST);
            }

            Page<Users> usersPage = userService.getUsers(offset, limit, sortBy, sortDirection, startDate, endDate, profession);
            return ResponseEntity.ok(usersPage);
        } catch (CustomException e) {
            logger.error("Error retrieving users: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorMessage = "Unexpected error occurred while retrieving users";
            logger.error(errorMessage, e);
            throw new CustomException("Failed to retrieve users", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID
     */
    @Operation(summary = "Retrieve a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + ApplicationConstants.GET_USERS + "')")
    public ResponseEntity<Users> getUserById(@PathVariable String id) {
        try {
            return userService.getUserById(id)
                    .map(user -> ResponseEntity.ok().body(user))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            String errorMessage = "Unexpected error occurred while retrieving user with id: " + id;
            logger.error(errorMessage, e);
            throw new CustomException("Failed to retrieve user", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new user.
     *
     * @param userRequest the user request containing user data
     * @return the created user
     */
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('" + ApplicationConstants.POST_USERS + "')")
    public ResponseEntity<Users> createUser(@RequestBody UserRequest userRequest) {
        try {
            Users savedUser = userService.addUser(userRequest);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            String errorMessage = "Unexpected error occurred while creating user";
            logger.error(errorMessage, e);
            throw new CustomException("Failed to create user", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing user.
     *
     * @param id          the ID of the user to update
     * @param userRequest the user request containing updated user data
     * @return the updated user
     */
    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + ApplicationConstants.PUT_USERS + "')")
    public ResponseEntity<Users> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {
        try {
            Users updatedUser = userService.updateUser(id, userRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            String errorMessage = "User not found with id: " + id;
            logger.error(errorMessage, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            String errorMessage = "Unexpected error occurred while updating user with id: " + id;
            logger.error(errorMessage, e);
            throw new CustomException("Failed to update user", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return a response indicating the result of the delete operation
     */
    @Operation(summary = "Delete a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + ApplicationConstants.DELETE_USERS + "')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            String errorMessage = "User not found with id: " + id;
            logger.error(errorMessage, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            String errorMessage = "Unexpected error occurred while deleting user with id: " + id;
            logger.error(errorMessage, e);
            throw new CustomException("Failed to delete user", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Uploads a CSV file to add or update users.
     *
     * @param file the CSV file containing user data
     * @return a response indicating the result of the upload operation
     */
    @Operation(summary = "Upload a CSV file to add or update users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded and data saved to database successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or data format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('" + ApplicationConstants.POST_USERS + "')")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a CSV file.");
        }

        try {
            userService.saveUsersFromCsv(file);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded and data saved to database successfully.");
        } catch (Exception e) {
            String errorMessage = "Failed to parse or save CSV file";
            logger.error(errorMessage, e);
            throw new CustomException("CSV upload failed", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
