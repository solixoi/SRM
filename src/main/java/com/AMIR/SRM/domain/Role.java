package com.AMIR.SRM.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    DIRECTOR,
    ADMIN,
    SUPPLIER;

    @Override
    public String getAuthority(){
        return name();
    }
}
