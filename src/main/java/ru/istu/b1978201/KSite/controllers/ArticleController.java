package ru.istu.b1978201.KSite.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class ArticleController {

    @GetMapping("article")
    public String getArticle(@ModelAttribute("id") String id, Model model) {

        System.out.println(id);

        if(id.isEmpty()){
            return "articlelist";
        }

        return "article";
    }

}
