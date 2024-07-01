package com.magmutual.users.constants;

import lombok.Getter;

@Getter
public enum UserRole {
    USER(ApplicationConstants.USER_ROLE),
    ADMIN(ApplicationConstants.ADMIN_ROLE);

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

}

