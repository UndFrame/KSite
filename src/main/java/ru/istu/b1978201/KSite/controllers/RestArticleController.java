package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.mode.Article;

import java.util.*;

@RestController()
public class RestArticleController {


    @Autowired
    private ArticleDao articleDao;

    @GetMapping(value = {"api/article"})
    public Map<String, Object> article(@RequestParam(value = "id", defaultValue = "") String hash) {


        if (hash.isEmpty()) return Collections.emptyMap();
        Article article = articleDao.findByHash(hash);
        if (article == null) return Collections.emptyMap();


        return getArticleJson(article);
    }



    @GetMapping(value = {"api/articles"})
    public Map<String, Object> articles(@RequestParam(value = "count", defaultValue = "") String countStr) {


        int count = countStr.isEmpty() ? 10 : Integer.parseInt(countStr);
        Page<Article> articles = articleDao.findAll(PageRequest.of(0, count, Sort.by(Sort.Order.desc("id"))));
        Map<String, Object> json = new HashMap<>();
        List<Map<String, Object>> articleList = new ArrayList<>();
        for (Article article : articles) {
            articleList.add(getArticleJson(article));
        }
        json.put("articles", articleList);
        return json;
    }

    private Map<String, Object> getArticleJson(Article article) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", article.getId());
        map.put("text", article.getText());
        map.put("description", article.getDescription());
        map.put("hash", article.getHash());
        map.put("likes", article.getLikes());
        map.put("dislikes", article.getDislikes());
        map.put("icon", article.getIconUrl());
        map.put("dateCreate", article.getDateCreate());
        map.put("author", article.getUser().getUsername());
        return map;
    }

}
