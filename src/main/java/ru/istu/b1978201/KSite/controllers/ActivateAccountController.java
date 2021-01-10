package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.istu.b1978201.KSite.services.UserService;

/**
 * Класс -контроллер предназначен для обработки запросоа связанного с активацией пользователя
 */
@Controller
public class ActivateAccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/activate")
    public String confirmationEmail(@RequestParam(value = "token") String token, Model model){
        if(token==null || token.isEmpty()){
            return "redirect:/reg";
        }
        boolean isActivated = userService.activateUser(token);
            model.addAttribute("activateToken",isActivated);

        return "login";
    }
}