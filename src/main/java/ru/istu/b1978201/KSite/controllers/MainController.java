package ru.istu.b1978201.KSite.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//TODO rename
@Controller
public class MainController implements WebMvcConfigurer {


    @GetMapping("/")
    public String main(Model model) {
        return "index";
    }

}
