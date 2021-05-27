package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.CommentDao;
import ru.istu.b1978201.KSite.dao.LikeDislikeDao;
import ru.istu.b1978201.KSite.encryption.JWT;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.Comment;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.ArticleService;
import ru.istu.b1978201.KSite.services.UserService;
import ru.istu.b1978201.KSite.uploadingfiles.StorageService;
import ru.istu.b1978201.KSite.utils.ArticleStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @Autowired
    private JWT jwt;


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
    public Map<String, Object> createArticle(HttpServletRequest requestS, @RequestParam(value = "access_token", defaultValue = "") String accessToken,
                                             @RequestParam(value = "description", defaultValue = "") String articleDescription,
                                             @RequestParam(value = "text", defaultValue = "") String articleText,
                                             @RequestParam(value = "icon", required = false) MultipartFile file
    ) {
        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            parameters.put(par[0], par[1]);
        }

        accessToken = parameters.get("access_token");
        Map<String, Object> request = new HashMap<>();

        if (articleDescription != null && !articleDescription.isEmpty()
                && articleText != null && !articleText.isEmpty()
                && file != null && !file.isEmpty()) {

            String contentType = file.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                if (!accessToken.isEmpty()) {
                    request.put("status", ArticleStatus.EXPIRED_TOKEN);
                    Optional<JSONObject> alivePayload = jwt.isAlive(accessToken);
                    if (alivePayload.isPresent()) {
                        request.put("status", ArticleStatus.TOKEN_DAMAGED);
                        try {
                            Long userIdOptional = alivePayload.get().getLong("uid");
                            User user = userService.findById(userIdOptional);
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
                                request.put("status", ArticleStatus.SUCCESSFULLY);
                            } else {
                                request.put("status", ArticleStatus.USER_NOT_EXIT);
                            }
                        } catch (JSONException ignored) {
                        }
                    }
                } else {
                    request.put("status", ArticleStatus.INPUT_FILE_NOT_ALLOWED);
                }
            } else {
                request.put("status", ArticleStatus.INPUT_DATA_IS_INVALID);
            }
        }

        if (articleDescription == null || articleDescription.isEmpty()) {
            request.put("status", ArticleStatus.INPUT_DESCRIPTION_IS_EMPTY);
        }
        if (articleText == null || articleText.isEmpty()) {
            request.put("status", ArticleStatus.INPUT_TEXT_IS_EMPTY);
        }
        if (file == null || file.isEmpty()) {
            request.put("status", ArticleStatus.INPUT_FILE_IS_NULL);
        }

        return request;
    }


    @PostMapping(value = {"api/commentarticle"})
    public Map<String, Object> createArticle(HttpServletRequest requestS, @RequestParam(value = "access_token", defaultValue = "") String accessToken,
                                             @RequestParam(value = "article_id", defaultValue = "") long articleId,
                                             @RequestParam(value = "comment", defaultValue = "") String comment
    ) {

        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            if (par.length != 2) continue;
            parameters.put(par[0], par[1]);
        }

        accessToken = parameters.get("access_token");
        articleId = Long.parseLong(parameters.get("article_id"));
        Map<String, Object> request = new HashMap<>();

        if (!accessToken.isEmpty() && !comment.isEmpty()) {
            request.put("status", ArticleStatus.EXPIRED_TOKEN);
            Optional<JSONObject> aliveToken = jwt.isAlive(accessToken);
            if (aliveToken.isPresent()) {
                request.put("status", ArticleStatus.TOKEN_DAMAGED);

                try {
                    Long userId = aliveToken.get().getLong("uid");
                    User user = userService.findById(userId);
                    if (user != null) {

                        Optional<Article> optionalArticle = articleDao.findById(articleId);
                        if (optionalArticle.isPresent()) {
                            Article article = optionalArticle.get();
                            Comment newComment = new Comment();
                            newComment.setComment(comment);
                            newComment.setArticle(article);
                            newComment.setUser(user);
                            article.getComment().add(newComment);
                            commentDao.save(newComment);
                            request.put("status", ArticleStatus.SUCCESSFULLY);
                        }
                    } else {
                        request.put("status", ArticleStatus.USER_NOT_EXIT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            request.put("status", ArticleStatus.INPUT_DATA_IS_INVALID);
        }

        return request;
    }

    @PostMapping(value = {"api/evaluation"})
    public Map<String, Object> like(HttpServletRequest requestS, @RequestParam(value = "access_token", defaultValue = "") String accessToken,
                                    @RequestParam(value = "article_id", defaultValue = "") long articleId,
                                    @RequestParam(value = "is_like", defaultValue = "") boolean isLike
    ) {
        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            if (par.length != 2) continue;
            parameters.put(par[0], par[1]);
        }

        accessToken = parameters.get("access_token");
        articleId = Long.parseLong(parameters.get("article_id"));
        isLike = Boolean.parseBoolean(parameters.get("is_like"));

        Map<String, Object> request = new HashMap<>();

        if (!accessToken.isEmpty()) {
            request.put("status", ArticleStatus.EXPIRED_TOKEN);
            Optional<JSONObject> aliveToken = jwt.isAlive(accessToken);
            if (aliveToken.isPresent()) {
                request.put("status", ArticleStatus.TOKEN_DAMAGED);
                try {
                    Long userId = aliveToken.get().getLong("uid");
                    User user = userService.findById(userId);
                    if (user != null) {

                        Optional<Article> optionalArticle = articleDao.findById(articleId);
                        if (optionalArticle.isPresent()) {
                            Article article = optionalArticle.get();
                            if (isLike)
                                articleService.likeArticle(user, article);
                            else
                                articleService.dislikeArticle(user, article);

                            request.put("likes", article.getLikes());
                            request.put("dislikes", article.getDislikes());
                            request.put("status", ArticleStatus.SUCCESSFULLY);
                        }
                    } else {
                        request.put("status", ArticleStatus.USER_NOT_EXIT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

        List<Map<String, Object>> comments = new ArrayList<>();

        for (Comment comment : article.getComment()) {
            Map<String, Object> commentJSON = new HashMap<>();
            commentJSON.put("id", comment.getId());
            commentJSON.put("author", comment.getUser().getId());
            commentJSON.put("article", comment.getArticle().getId());
            commentJSON.put("text", comment.getComment());
            comments.add(commentJSON);
        }
        map.put("comments", comments);

        return map;
    }

}
