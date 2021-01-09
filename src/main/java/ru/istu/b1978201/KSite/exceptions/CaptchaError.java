package ru.istu.b1978201.KSite.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * Ошибка, когда пользователь не смог пройти капчу
 */
public class CaptchaError extends AuthenticationException {
    public CaptchaError(String msg, Throwable t) {
        super(msg, t);
    }

    public CaptchaError(String msg) {
        super(msg);
    }
}
