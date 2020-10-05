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
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.MailService;
import ru.istu.b1978201.KSite.services.UserService;

@Controller
public class MailInfoController {

    @Value("${RECAPTCHA_SITE_KEY}")
    private String captchaSiteKey;
    @Value("${RECAPTCHA_SERVER_KEY}")
    private String captchaServerKey;
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/tokenInfo")
    public String getTokenInfo(Model model) {
        return "sendTokenInfo";
    }

    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;

    @PostMapping("/tokenInfo")
    public String sendToken(
            @RequestParam(value = "recaptchaResponse", required = false) String recaptchaResponse,
            @RequestParam(value = "email", required = false) String email,
            @ModelAttribute("userForm") User userForm,
            Model model
    ) {

        String captchaParam = "?secret=" + captchaServerKey + "&response=" + recaptchaResponse;
        String captchaURL = "https://www.google.com/recaptcha/api/siteverify";
        ReCaptchaResponse captchaResponse = restTemplate.exchange(captchaURL + captchaParam, HttpMethod.POST, null, ReCaptchaResponse.class).getBody();
        model.addAttribute("RECAPTCHA_SITE_KEY", captchaSiteKey);
        model.addAttribute("email", email);
        if (captchaResponse == null || !captchaResponse.isSuccess()) {
            model.addAttribute("captchaError", true);
            return "sendTokenInfo";
        }
        User user = userService.findByEmail(email);
        if(user!=null) {
            if(user.getToken()==null){
                return "redirect:/";
            }else {
                if((System.currentTimeMillis() - user.getToken().getDate().getTime())/1000 >=30)
                mailService.sendRegisterToken(user);
                return "sendTokenInfo";
            }
        }
        return "reg";
    }

}
