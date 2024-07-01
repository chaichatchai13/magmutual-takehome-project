package com.magmutual.users.utils;

import com.magmutual.users.constants.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * Converts a date string to a Timestamp object.
     *
     * @param dateString the date string to convert
     * @return the corresponding Timestamp object
     * @throws RuntimeException if the date string is in an invalid format
     */
    public static Timestamp convertStringToDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT);
        try {
            Date utilDate = formatter.parse(dateString);
            return new Timestamp(utilDate.getTime());
        } catch (ParseException e) {
            String errorMessage = "Invalid date format: " + dateString;
            logger.error(errorMessage, e);
            throw new RuntimeException("Invalid date format", e);
        }
    }
}
