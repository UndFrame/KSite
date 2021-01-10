package ru.istu.b1978201.KSite.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Ошибка возникающая когда забаненый пользователь пытается что-то делать на сайте
 */
public class UserIsBanned extends AuthenticationException {
    public UserIsBanned(String msg) {
        super(msg);
    }
}
