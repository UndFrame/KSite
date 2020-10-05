package ru.istu.b1978201.KSite.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public class CustomSecurityContext implements SecurityContext {
    @Override
    public Authentication getAuthentication() {
        return null;
    }

    @Override
    public void setAuthentication(Authentication authentication) {

    }
}
