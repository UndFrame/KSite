package ru.istu.b1978201.KSite.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.istu.b1978201.KSite.captcha.ReCaptchaResponse;
import ru.istu.b1978201.KSite.exceptions.CaptchaError;
import ru.istu.b1978201.KSite.exceptions.UserIsBanned;
import ru.istu.b1978201.KSite.mode.Role;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.UserService;
import java.util.ArrayList;
import java.util.List;
/**
Класс реализующий обратобку аутентификации пользователя.
 */
@Component
public class AuthProviderImpl implements AuthenticationProvider {

    @Value("${RECAPTCHA_SITE_KEY}")
    private String captchaSiteKey;
    @Value("${RECAPTCHA_SERVER_KEY}")
    private String captchaServerKey;
    private String captchaURL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        CustomWebAuthenticationDetails webAuthenticationDetails = (CustomWebAuthenticationDetails)authentication.getDetails();
//        String captchaParam = "?secret="+captchaServerKey+"&response="+ webAuthenticationDetails.getCaptchaToken();
//        ReCaptchaResponse captchaResponse = restTemplate.exchange(captchaURL + captchaParam, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();
//        if(captchaResponse==null || !captchaResponse.isSuccess()){
//            throw new CaptchaError("captcha error");
//        }
        User user = userService.findByUsername(authentication.getName());
        if(user == null){
            user = userService.findByEmail(authentication.getName());
        }
        if(user==null){
            throw new UsernameNotFoundException("user not found");
        }
        if(!user.isEnabled()){
            throw new LockedException("user not activate");
        }
        if(user.isBan()){
            throw new UserIsBanned("user is banned");
        }
        if(!passwordEncoder.matches(authentication.getCredentials().toString(),user.getPassword())){
            throw new BadCredentialsException("password not equals");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_BEGINNER"));
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
        }
        return new UsernamePasswordAuthenticationToken(user,null,grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
