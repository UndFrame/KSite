package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.istu.b1978201.KSite.dao.UserDao;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class RestAuthController {

    @Autowired
    public UserDao userDao;


    @PostMapping(value = {"api/auth"})
    public Map<String, Object> article(@RequestParam(value = "login" , defaultValue = "") String login,
                                       @RequestParam(value = "password", defaultValue = "")String password) {

        System.out.println("API AUTH");

        Map<String, Object> json = new HashMap<>();

        json.put("refresh_token", login+UUID.randomUUID().toString());
        json.put("access_token", password+UUID.randomUUID().toString());

        return json;
    }

    @GetMapping(value = {"api/auth"})
    public Map<String, Object> articleGet(@RequestParam(value = "login" ,defaultValue = "") String login,
                                       @RequestParam(value = "password" , defaultValue = "")String password) {

        System.out.println("API AUTH");

        Map<String, Object> json = new HashMap<>();

        json.put("refresh_token", login+UUID.randomUUID().toString());
        json.put("access_token", password+UUID.randomUUID().toString());

        return json;
    }



}
