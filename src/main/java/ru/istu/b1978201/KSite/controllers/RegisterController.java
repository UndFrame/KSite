package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.istu.b1978201.KSite.captcha.ReCaptchaResponse;
import ru.istu.b1978201.KSite.services.UserService;
import ru.istu.b1978201.KSite.mode.User;

import java.util.regex.Pattern;

/**
 * Класс -контроллер предназвачен для обработки запросоа регистрации
 */
@Controller
public class RegisterController {

    @Value("${RECAPTCHA_SITE_KEY}")
    private String captchaSiteKey;
    @Value("${RECAPTCHA_SERVER_KEY}")
    private String captchaServerKey;
    private String captchaURL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/reg")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("error", "error not found sa");
        model.addAttribute("RECAPTCHA_SITE_KEY", captchaSiteKey);
        return "reg";
    }


    @PostMapping("/reg")
    public String registrationUser(
            @RequestParam("passwordConfirm") String confirmPassword,
            @RequestParam("recaptchaResponse") String recaptchaResponse,
            @ModelAttribute("userForm") User userForm,
            Model model
    ) {
        String captchaParam = "?secret=" + captchaServerKey + "&response=" + recaptchaResponse;
        ReCaptchaResponse captchaResponse = restTemplate.exchange(captchaURL + captchaParam, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();
        model.addAttribute("RECAPTCHA_SITE_KEY", captchaSiteKey);
        if (captchaResponse == null || !captchaResponse.isSuccess()) {
            model.addAttribute("captchaError", true);
            return "reg";
        }
        boolean error = false;
        String email = userForm.getEmail();

        //TODO create validator
        if (userForm.getUsername() == null || userForm.getUsername().isEmpty()) {
            error = true;
            model.addAttribute("usernameNull", true);
            model.addAttribute("usernameError", true);
        } else if (userForm.getUsername().length() < 4) {
            error = true;
            model.addAttribute("smallUsername", true);
            model.addAttribute("usernameError", true);
        } else if (userForm.getUsername().length() > 15) {
            error = true;
            model.addAttribute("bigUserName", true);
            model.addAttribute("usernameError", true);
        }

        if (email == null || email.isEmpty()) {
            error = true;
            model.addAttribute("emailNull", true);
            model.addAttribute("emailError", true);
        } else if (!isValidEmail(email)) {
            error = true;
            model.addAttribute("emailNoValid", true);
            model.addAttribute("emailError", true);
        }

        if (userForm.getPassword() == null) {
            error = true;
            model.addAttribute("passwordNull", true);
            model.addAttribute("passwordError", true);
        }

        if (userForm.getPassword().length() < 8) {
            error = true;
            model.addAttribute("smallPassword", true);
            model.addAttribute("passwordError", true);
        }
        if (userForm.getPassword().length() > 32) {
            error = true;
            model.addAttribute("bigPassword", true);
            model.addAttribute("passwordError", true);
        }

        if (confirmPassword == null || confirmPassword.isEmpty() || !confirmPassword.equals(userForm.getPassword())) {
            error = true;
            model.addAttribute("confirmPasswordEquals", true);
            model.addAttribute("confirmPasswordError", true);
        }

        if (error) {
            model.addAttribute("usernameOld", userForm.getUsername() != null ? userForm.getUsername() : "");
            model.addAttribute("passwordOld", userForm.getPassword() != null ? userForm.getPassword() : "");
            model.addAttribute("emailOld", userForm.getEmail() != null ? userForm.getEmail() : "");

            return "reg";
        }
        userForm.setUsername(userForm.getUsername().toLowerCase());
        userForm.setEmail(userForm.getEmail().toLowerCase());
        boolean create = userService.createUser(userForm);
        if (create) {
            model.addAttribute("email", userForm.getEmail());
            return "sendTokenInfo";
        }
        model.addAttribute("alreadyExist", true);
        return "reg";
    }


    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
