package ru.istu.b1978201.KSite.services;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

public class CustomSecurityHolder implements SecurityContextHolderStrategy {
    @Override
    public void clearContext() {

    }

    @Override
    public SecurityContext getContext() {
        return null;
    }

    @Override
    public void setContext(SecurityContext context) {

    }

    @Override
    public SecurityContext createEmptyContext() {
        return null;
    }
}
