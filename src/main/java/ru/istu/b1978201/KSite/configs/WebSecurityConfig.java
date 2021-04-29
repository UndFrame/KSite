package ru.istu.b1978201.KSite.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.istu.b1978201.KSite.authentication.AuthProviderImpl;
import ru.istu.b1978201.KSite.authentication.CustomAuthenticationFailureHandler;
import ru.istu.b1978201.KSite.authentication.CustomWebAuthenticationDetailsSource;
import ru.istu.b1978201.KSite.exceptions.CaptchaError;
import ru.istu.b1978201.KSite.exceptions.UserIsBanned;
import ru.istu.b1978201.KSite.uploadingfiles.FileSystemStorageService;
import ru.istu.b1978201.KSite.uploadingfiles.StorageService;

/**
 * Класс - конфиг, в котором определяющтся основные свойста работы приложения,описываются бины для загрузки с файлами.
 * Описывается доступ к тем или иным рессурсам
 */
@Configuration
@EnableWebSecurity
@PropertySource("classpath:secret.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthProviderImpl authProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/","/fload").permitAll()
                .antMatchers("/api/*").permitAll()
                .antMatchers("/login", "/reg", "/activate", "/tokenInfo", "/activate").anonymous()
                .antMatchers("/account","/editor").authenticated()
                .antMatchers("/form").hasAnyRole("ADMIN")
                .antMatchers("/*").permitAll()
                .and()
                .httpBasic(httpSecurityHttpBasicConfigurer -> {

                })
                .formLogin()
                .authenticationDetailsSource(customAuthenticationFailureHandler())
                .failureHandler(new AuthenticationEntryPointFailureHandler((request, response, authException) -> {
                    String redirect = "/login?error";
                    if (authException instanceof CaptchaError) {
                        redirect = "/login?captcha";
                    }
                    if (authException instanceof UserIsBanned) {
                        redirect = "/login?ban";
                    }
                    response.sendRedirect(redirect);
                }))
                .usernameParameter("userId")
                .loginPage("/login")
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
                .and();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }


    @Bean
    public CustomWebAuthenticationDetailsSource customAuthenticationFailureHandler() {
        return new CustomWebAuthenticationDetailsSource();
    }

    @Bean
    public StorageService storageService() {
        return new FileSystemStorageService();
    }

    @Bean(name = "sessionRegistry")
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
