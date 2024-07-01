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
            setUserProperties(user, convertUserRequestToMap(userRequest));
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
        Users user = new Users();
        setUserProperties(user, convertUserRequestToMap(userRequest));
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
                setUserProperties(user, convertCSVRecordToMap(csvRecord));
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
     * Sets the properties of a Users entity from a map of properties.
     *
     * @param user the Users entity
     * @param properties the map of properties
     * @return the Users entity with properties set
     */
    private Users setUserProperties(Users user, Map<String, String> properties) {
        user.setId(properties.get(UserField.ID.getFieldName()) != null ? Long.parseLong(properties.get(UserField.ID.getFieldName())) : null);
        user.setFirstname(properties.get(UserField.FIRSTNAME.getFieldName()));
        user.setLastname(properties.get(UserField.LASTNAME.getFieldName()));
        user.setEmail(properties.get(UserField.EMAIL.getFieldName()));
        user.setProfession(properties.get(UserField.PROFESSION.getFieldName()));
        user.setDateCreated(properties.get(UserField.DATE_CREATED.getFieldName()) != null ? DateUtil.convertStringToDate(properties.get(UserField.DATE_CREATED.getFieldName())) : null);
        user.setCountry(properties.get(UserField.COUNTRY.getFieldName()));
        user.setCity(properties.get(UserField.CITY.getFieldName()));
        return user;
    }

    /**
     * Converts a UserRequest object to a map of properties.
     *
     * @param userRequest the UserRequest object
     * @return a map of properties
     */
    private Map<String, String> convertUserRequestToMap(UserRequest userRequest) {
        Map<String, String> properties = new HashMap<>();
        properties.put(UserField.ID.getFieldName(), String.valueOf(userRequest.getId()));
        properties.put(UserField.FIRSTNAME.getFieldName(), userRequest.getFirstname());
        properties.put(UserField.LASTNAME.getFieldName(), userRequest.getLastname());
        properties.put(UserField.EMAIL.getFieldName(), userRequest.getEmail());
        properties.put(UserField.PROFESSION.getFieldName(), userRequest.getProfession());
        properties.put(UserField.DATE_CREATED.getFieldName(), userRequest.getDateCreated());
        properties.put(UserField.COUNTRY.getFieldName(), userRequest.getCountry());
        properties.put(UserField.CITY.getFieldName(), userRequest.getCity());
        return properties;
    }

    /**
     * Converts a CSVRecord object to a map of properties.
     *
     * @param csvRecord the CSVRecord object
     * @return a map of properties
     */
    private Map<String, String> convertCSVRecordToMap(CSVRecord csvRecord) {
        Map<String, String> properties = new HashMap<>();
        properties.put(UserField.ID.getFieldName(), csvRecord.get(UserField.ID.getFieldName()));
        properties.put(UserField.FIRSTNAME.getFieldName(), csvRecord.get(UserField.FIRSTNAME.getFieldName()));
        properties.put(UserField.LASTNAME.getFieldName(), csvRecord.get(UserField.LASTNAME.getFieldName()));
        properties.put(UserField.EMAIL.getFieldName(), csvRecord.get(UserField.EMAIL.getFieldName()));
        properties.put(UserField.PROFESSION.getFieldName(), csvRecord.get(UserField.PROFESSION.getFieldName()));
        properties.put(UserField.DATE_CREATED.getFieldName(), csvRecord.get(UserField.DATE_CREATED.getFieldName()));
        properties.put(UserField.COUNTRY.getFieldName(), csvRecord.get(UserField.COUNTRY.getFieldName()));
        properties.put(UserField.CITY.getFieldName(), csvRecord.get(UserField.CITY.getFieldName()));
        return properties;
    }
}
