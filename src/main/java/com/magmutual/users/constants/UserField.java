package com.magmutual.users.constants;

public enum UserField {
    ID("id"),
    FIRSTNAME("firstname"),
    LASTNAME("lastname"),
    EMAIL("email"),
    PROFESSION("profession"),
    DATE_CREATED("dateCreated"),
    COUNTRY("country"),
    CITY("city");

    private final String fieldName;

    UserField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}

