package ru.istu.b1978201.KSite.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserIsBanned extends AuthenticationException {
    public UserIsBanned(String msg) {
        super(msg);
    }
}
