package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.CommentDao;
import ru.istu.b1978201.KSite.dao.LikeDislikeDao;
import ru.istu.b1978201.KSite.encryption.JWT;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.ArticleService;
import ru.istu.b1978201.KSite.services.UserService;
import ru.istu.b1978201.KSite.uploadingfiles.StorageService;
import ru.istu.b1978201.KSite.utils.ArticleStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@RestController()
public class RestArticleController {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeDislikeDao likeDislikeDao;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ArticleService articleService;


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

    @PostMapping(value = {"api/createarticle"})
    public Map<String, Object> createArticle(HttpServletRequest requestS,@RequestParam(value = "access_token", defaultValue = "") String accessToken,
                                             @RequestParam(value = "description", defaultValue = "") String articleDescription,
                                             @RequestParam(value = "text", defaultValue = "") String articleText,
                                             @RequestParam(value = "icon") MultipartFile file
                                             ) {

        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=",2);
            parameters.put(par[0], par[1]);
        }

        accessToken = parameters.get("access_token");
        articleDescription = parameters.get("description");
        articleText = parameters.get("text");
        Map<String, Object> request = new HashMap<>();

        if(!accessToken.isEmpty() && !articleDescription.isEmpty() && !articleText.isEmpty() && file!=null) {
            request.put("status", ArticleStatus.EXPIRED_TOKEN);
            if (JWT.isAlive(accessToken)) {
                request.put("status", ArticleStatus.TOKEN_DAMAGED);
                Optional<Long> userIdOptional = JWT.getUserId(accessToken);
                if(userIdOptional.isPresent()){
                    User user = userService.findById(userIdOptional.get());
                    if (user != null) {
                        Article article = new Article();
                        article.setText(articleText);
                        article.setDescription(articleDescription);
                        article.setHash(UUID.randomUUID().toString());
                        article.setUser(user);
                        article.setIcon(file.getOriginalFilename());
                        article.setDateCreate(new Date());
                        articleDao.save(article);
                        storageService.store(file);
                        request.put("status", ArticleStatus.SUCCESSFULLY_CREATED);
                    } else {
                        request.put("status", ArticleStatus.USER_NOT_EXIT);
                    }
                }
            }
        }else{
            request.put("status", ArticleStatus.INPUT_DATA_IS_INVALID);
        }

        return request;
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
