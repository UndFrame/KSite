package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.mode.Article;

@Controller
public class ArticleController {

    @Autowired
    private ArticleDao articleDao;

    @GetMapping("article")
    public String getArticle(@ModelAttribute("id") String id, Model model) {
        if(id.isEmpty()){

            Page<Article> all = articleDao.findAll(PageRequest.of(0, 10));

            model.addAttribute("articles",all);

            return "articlelist";
        }

        Article article = articleDao.findByHash(id);

        model.addAttribute("findArticle",article!=null);
        model.addAttribute("article",article);

        return "article";
    }

}
