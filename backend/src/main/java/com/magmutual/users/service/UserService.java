package com.magmutual.users.service;

import com.magmutual.users.constants.ApplicationConstants;
import com.magmutual.users.constants.UserField;
import com.magmutual.users.entity.Users;
import com.magmutual.users.exception.CustomException;
import com.magmutual.users.model.UserRequest;
import com.magmutual.users.repository.UserRepository;
import com.magmutual.users.utils.DateUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Fetches a user by ID.
     *
     * @param id the user ID
     * @return an Optional containing the user if found
     */
    public Optional<Users> getUserById(String id) {
        logger.debug("Fetching user with id: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Updates a user by ID.
     *
     * @param id the user ID
     * @param userRequest the user data to update
     * @return the updated user
     */
    public Users updateUser(String id, UserRequest userRequest) {
        Optional<Users> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            mapUserRequestToUser(user, userRequest);
            logger.debug("Updating user with id: {}", id);
            return userRepository.save(user);
        } else {
            String errorMessage = ApplicationConstants.USER_NOT_FOUND + ": " + id;
            logger.error(errorMessage);
            throw new CustomException(ApplicationConstants.USER_NOT_FOUND, errorMessage, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     */
    public void deleteUser(String id) {
        logger.debug("Deleting user with id: {}", id);
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            String errorMessage = ApplicationConstants.USER_DELETION_FAILED + ": " + id;
            logger.error(errorMessage, e);
            throw new CustomException(ApplicationConstants.USER_DELETION_FAILED, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches a paginated list of users with filters and sorting options.
     *
     * @param offset the page offset
     * @param limit the page limit
     * @param sortBy the field to sort by
     * @param sortDirection the sort direction
     * @param startDate the start date filter
     * @param endDate the end date filter
     * @param profession the profession filter
     * @return a paginated list of users
     */
    public Page<Users> getUsers(int offset, int limit, String sortBy, String sortDirection, Date startDate, Date endDate, String profession) {
        logger.debug("Fetching users with filters: offset={}, limit={}, sortBy={}, sortDirection={}, startDate={}, endDate={}, profession={}", offset, limit, sortBy, sortDirection, startDate, endDate, profession);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), sortBy);
        Pageable pageable = PageRequest.of(offset / limit, limit, sort);

        if (startDate != null && endDate != null && profession != null) {
            return userRepository.findByDateCreatedBetweenAndProfession(startDate, endDate, profession, pageable);
        } else if (startDate != null && endDate != null) {
            return userRepository.findByDateCreatedBetween(startDate, endDate, pageable);
        } else if (profession != null) {
            return userRepository.findByProfession(profession, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    /**
     * Adds a new user.
     *
     * @param userRequest the user data to add
     * @return the added user
     */
    public Users addUser(UserRequest userRequest) {
        if (userRepository.existsById(String.valueOf(userRequest.getId()))) {
            logger.error("User with id {} already exists", userRequest.getId());
            throw new CustomException("User already exists with id: " + userRequest.getId(), "Conflict", HttpStatus.CONFLICT);
        }
        Users user = new Users();
        mapUserRequestToUser(user, userRequest);
        logger.debug("Adding new user with id: {}", user.getId());
        return userRepository.save(user);
    }

    /**
     * Saves users from a CSV file.
     *
     * @param file the CSV file
     */
    @Transactional
    public void saveUsersFromCsv(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                Users user = new Users();
                mapCsvRecordToUser(user, csvRecord);
                userRepository.save(user);
            }
            logger.debug("Successfully parsed and saved users from CSV file");
        } catch (IOException e) {
            String errorMessage = ApplicationConstants.CSV_PARSE_ERROR;
            logger.error(errorMessage, e);
            throw new CustomException("CSV parsing failed", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Maps a UserRequest object to a Users entity.
     *
     * @param userRequest the UserRequest object
     * @param user the Users entity
     */
    private void mapUserRequestToUser(Users user, UserRequest userRequest) {
        user.setId(userRequest.getId());
        user.setFirstname(userRequest.getFirstname());
        user.setLastname(userRequest.getLastname());
        user.setEmail(userRequest.getEmail());
        user.setProfession(userRequest.getProfession());
        user.setDateCreated(userRequest.getDateCreated() != null ? DateUtil.convertStringToDate(userRequest.getDateCreated()) : null);
        user.setCountry(userRequest.getCountry());
        user.setCity(userRequest.getCity());
    }

    /**
     * Maps a CSVRecord object to a Users entity.
     *
     * @param csvRecord the CSVRecord object
     * @param user the Users entity
     */
    private void mapCsvRecordToUser(Users user, CSVRecord csvRecord) {
        user.setId(Long.parseLong(csvRecord.get(UserField.ID.getFieldName())));
        user.setFirstname(csvRecord.get(UserField.FIRSTNAME.getFieldName()));
        user.setLastname(csvRecord.get(UserField.LASTNAME.getFieldName()));
        user.setEmail(csvRecord.get(UserField.EMAIL.getFieldName()));
        user.setProfession(csvRecord.get(UserField.PROFESSION.getFieldName()));
        user.setDateCreated(DateUtil.convertStringToDate(csvRecord.get(UserField.DATE_CREATED.getFieldName())));
        user.setCountry(csvRecord.get(UserField.COUNTRY.getFieldName()));
        user.setCity(csvRecord.get(UserField.CITY.getFieldName()));
    }
}
