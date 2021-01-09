package ru.istu.b1978201.KSite.authentication;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
/**
 * Класс который наследует WebAuthenticationDetails и добавляет поле captchaToken
 */
public class CustomWebAuthenticationDetails  extends WebAuthenticationDetails {

    private String captchaToken;

    public CustomWebAuthenticationDetails(HttpServletRequest request, String token) {
        super(request);
        this.captchaToken = token;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    @Override
    public String toString() {
        return "CustomWebAuthenticationDetails{" +
                "captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
