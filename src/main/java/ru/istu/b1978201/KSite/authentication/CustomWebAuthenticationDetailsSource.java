package ru.istu.b1978201.KSite.authentication;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

public class CustomWebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest,CustomWebAuthenticationDetails> {

    @Override
    public CustomWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        String recaptchaResponse = context
                .getParameter("recaptchaResponse");
        return new CustomWebAuthenticationDetails(context,recaptchaResponse);
    }

}
