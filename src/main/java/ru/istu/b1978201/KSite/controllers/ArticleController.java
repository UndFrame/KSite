package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.CommentDao;
import ru.istu.b1978201.KSite.dao.LikeDislikeDao;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.Comment;
import ru.istu.b1978201.KSite.mode.LikeDislike;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.uploadingfiles.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Controller
public class ArticleController {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private LikeDislikeDao likeDislikeDao;

    @Autowired
    private StorageService storageService;


    @GetMapping("article")
    public String getArticle(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);

        Page<Article> all = articleDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"))));
        model.addAttribute("articles", all);
        return "articlelist";

    }

    @GetMapping("/article/{id:.+}")
    public String serveFile(@PathVariable String id, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);

        Article article = articleDao.findByHash(id);
        if (article != null) {
            model.addAttribute("url", "http://localhost:8080/files/" + article.getIcon());
            model.addAttribute("timeCreate", article.getDateCreate().toString());
        }
        model.addAttribute("findArticle", article != null);
        model.addAttribute("article", article);
        if (article != null) {
            model.addAttribute("comments", article.getComment());
        }

        return "article";
    }

    @PostMapping(value = "article", params = "delete")
    public String deleteArticle(@ModelAttribute("id") String id, Model model) {
        Article article = articleDao.findByHash(id);

        if (article != null) {
            commentDao.deleteAll(article.getComment());
            likeDislikeDao.deleteAll(article.getLikeDislikes());
            articleDao.delete(article);
        }

        return "redirect:/article";
    }

    @PostMapping(value = "article", params = "editor")
    public String addComment(@ModelAttribute("id") String id, @ModelAttribute("comment") String comment, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);

        if (id.isEmpty()) {

            Page<Article> all = articleDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"))));
            model.addAttribute("articles", all);

            return "articlelist";
        }

        Article article = articleDao.findByHash(id);
        model.addAttribute("findArticle", article != null);
        model.addAttribute("article", article);


        if (article != null && user != null && !comment.isEmpty()) {
            Comment newComment = new Comment();
            newComment.setComment(comment);
            newComment.setArticle(article);
            newComment.setUser(user);
            article.getComment().add(newComment);
            commentDao.save(newComment);
            model.addAttribute("comments", article.getComment());
        }

        return "article";
    }

    @PostMapping(value = "article", params = "dislike")
    public String dislike(@ModelAttribute("id") String id, @ModelAttribute("comment") String comment, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);

        if (id.isEmpty()) {

            Page<Article> all = articleDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"))));
            model.addAttribute("articles", all);

            return "articlelist";
        }

        Article article = articleDao.findByHash(id);
        model.addAttribute("findArticle", article != null);
        model.addAttribute("article", article);


        if (article != null && user != null) {
            LikeDislike likeDislike = null;

            for (LikeDislike dislike : user.getLikeDislikes()) {
                if (dislike.getArticle().equals(article)) {
                    likeDislike = dislike;
                }
            }
            boolean newLike = false;

            if (likeDislike == null) {
                likeDislike = new LikeDislike();
                newLike = true;
            }
            if (!likeDislike.isDislike()) {
                likeDislike.setArticle(article);
                likeDislike.setUser(user);
                likeDislike.setDislike(true);


                article.setDislikes(article.getDislikes() + 1);
                if (!newLike)
                    article.setLikes(article.getLikes() - 1);

                article.getLikeDislikes().add(likeDislike);
                user.getLikeDislikes().add(likeDislike);
                likeDislikeDao.save(likeDislike);
                articleDao.save(article);
            }
            model.addAttribute("comments", article.getComment());
        }

        return "article";
    }

    @PostMapping(value = "article", params = "like")
    public String like(@ModelAttribute("id") String id, @ModelAttribute("comment") String comment, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);

        if (id.isEmpty()) {

            Page<Article> all = articleDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"))));
            model.addAttribute("articles", all);

            return "articlelist";
        }

        Article article = articleDao.findByHash(id);
        model.addAttribute("findArticle", article != null);
        model.addAttribute("article", article);


        if (article != null && user != null) {
            LikeDislike likeDislike = null;

            for (LikeDislike dislike : user.getLikeDislikes()) {
                if (dislike.getArticle().equals(article)) {
                    likeDislike = dislike;
                }
            }

            boolean newLike = false;

            if (likeDislike == null) {
                likeDislike = new LikeDislike();
                newLike = true;
            }
            if (!likeDislike.isLike()) {
                likeDislike.setArticle(article);
                likeDislike.setUser(user);
                likeDislike.setLike(true);


                article.setLikes(article.getLikes() + 1);
                if (!newLike)
                    article.setDislikes(article.getDislikes() - 1);
                article.getLikeDislikes().add(likeDislike);
                user.getLikeDislikes().add(likeDislike);
                likeDislikeDao.save(likeDislike);
                articleDao.save(article);
            }
            model.addAttribute("comments", article.getComment());
        }

        return "article";
    }

    @GetMapping("editor")
    public String getEditor(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);

        Page<Article> all = articleDao.findAll(PageRequest.of(0, 4, Sort.by(Sort.Order.desc("id"))));
        model.addAttribute("articles", all);
        return "editor";
    }


    @PostMapping("/editor")
    public String handleFileUpload(@ModelAttribute("description") String description,
                                   @ModelAttribute("text") String text,
                                   @RequestParam("file") MultipartFile file,
                                   Model redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        redirectAttributes.addAttribute("auth", user != null);
        redirectAttributes.addAttribute("user", user);

        if (user != null) {
            Article article = new Article();
            article.setText(text);
            article.setDescription(description);
            article.setHash(UUID.randomUUID().toString());
            article.setUser(user);
            article.setIcon(file.getOriginalFilename());
            article.setDateCreate(new Date());

            articleDao.save(article);
            storageService.store(file);
        }

        Page<Article> all = articleDao.findAll(PageRequest.of(0, 4, Sort.by(Sort.Order.desc("id"))));
        redirectAttributes.addAttribute("articles", all);

        return "editor";
    }


    @Value("${FILE_SOURCE}")
    private String location;


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
