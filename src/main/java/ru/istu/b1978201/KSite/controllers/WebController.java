package ru.istu.b1978201.KSite.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.RoleDao;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.Role;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.UserService;
import ru.istu.b1978201.KSite.uploadingfiles.StorageService;
import ru.istu.b1978201.KSite.utils.CheckedRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@Configuration
public class WebController implements WebMvcConfigurer {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleDao roleDao;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }

    @GetMapping("/index")
    public String index(Model model) {
        return "redirect:/";
    }


    @Autowired
    private ArticleDao articleDao;

    @GetMapping("/")
    public String main(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);


        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        return main(model);
    }


    @GetMapping("/form")
    public String showForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);
        return "form";
    }

    @GetMapping("/;")
    public String test() {
        return "redirect:/";
    }

    @PostMapping("/form")
    public String checkPersonInfo(
            @RequestParam("username") String username,
            Model model
    ) {

        if (username != null) {
            User user = userService.findByUsername(username);
            if (user != null) {
                model.addAttribute("finduser", true);
                model.addAttribute("username", user.getUsername());

                List<CheckedRole> checkedRoleList = new ArrayList<>();

                roleDao.findAll().forEach(role -> {
                    if (user.getRoles().contains(role)) {
                        checkedRoleList.add(new CheckedRole(role, true));
                    } else {
                        checkedRoleList.add(new CheckedRole(role, false));
                    }
                });

                model.addAttribute("roles", checkedRoleList);
                /*model.addAttribute("finduser",true);
                model.addAttribute("finduser",true);
                model.addAttribute("finduser",true);*/

            }
        }
        System.out.println("un: " + username);

        return "form";
    }

    @GetMapping("/account")
    public String account(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);
        model.addAttribute("roles", user != null ? user.getRoles().toArray(new Role[]{}) : Collections.emptyList());

        List<Article> articles = new ArrayList<>();

        if (user != null) {
            articles = articleDao.findAllByUserId(user.getId());
        }

        model.addAttribute("articles", articles);
        return "user";
    }

    @Autowired
    private StorageService storageService;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations(storageService.getProperties().getLocation());
    }


}
