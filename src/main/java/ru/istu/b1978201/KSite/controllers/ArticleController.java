package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.CommentDao;
import ru.istu.b1978201.KSite.dao.LikeDislikeDao;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.Comment;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.ArticleService;
import ru.istu.b1978201.KSite.uploadingfiles.StorageService;

import java.util.Date;
import java.util.UUID;

/**
 * Класс -контроллер предназначен для обработки запросов связанных со статьями(создание,просмотр, дать оценку)
 */
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

    @Autowired
    private ArticleService articleService;




    @GetMapping("article")
    public String getArticle(@RequestParam(value = "id",defaultValue = "") String id,Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);


        if(id.isEmpty()) {
            Page<Article> all = articleDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"))));
            model.addAttribute("articles", all);
            return "articlelist";
        }else{
            Article article = articleDao.findByHash(id);
            if (article != null) {
                model.addAttribute("url", article.getIconUrl());
                model.addAttribute("timeCreate", article.getDateCreate().toString());
            }
            model.addAttribute("findArticle", article != null);
            model.addAttribute("article", article);
            if (article != null) {
                model.addAttribute("comments", article.getComment());
            }

            return "article";
        }
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
        if (article != null) {
            model.addAttribute("url", article.getIconUrl());
            model.addAttribute("timeCreate", article.getDateCreate().toString());
        }
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
        if (article != null) {
            model.addAttribute("url", article.getIconUrl());
            model.addAttribute("timeCreate", article.getDateCreate().toString());
        }
        model.addAttribute("findArticle", article != null);
        model.addAttribute("article", article);


        if (article != null && user != null) {
            articleService.dislikeArticle(user, article);
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
        if (article != null) {
            model.addAttribute("url", article.getIconUrl());
            model.addAttribute("timeCreate", article.getDateCreate().toString());
        }
        model.addAttribute("findArticle", article != null);
        model.addAttribute("article", article);


        if (article != null && user != null) {
            articleService.likeArticle(user, article);
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


    /**
     *
     * @param filename
     * @return
     */
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
