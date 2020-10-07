package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.CommentDao;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.Comment;
import ru.istu.b1978201.KSite.mode.User;

import java.util.UUID;

@Controller
public class ArticleController {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private CommentDao commentDao;

    @GetMapping("article")
    public String getArticle(@ModelAttribute("id") String id, Model model) {

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
        if (article != null) {
            model.addAttribute("comments", article.getComment());
        }

        return "article";
    }

    @PostMapping("article")
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


        if (article != null && user != null) {
            Comment newComment = new Comment();
            newComment.setComment(comment);
            newComment.setArticle(article);
            newComment.setUser(user);
            commentDao.save(newComment);
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


    @PostMapping("editor")
    public String getEditor(@ModelAttribute("description") String description, @ModelAttribute("text") String text, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = authentication.getPrincipal() instanceof User ? (User) authentication.getPrincipal() : null;
        model.addAttribute("auth", user != null);
        model.addAttribute("user", user);

        if (user != null) {
            Article article = new Article();
            article.setText(text);
            article.setDescription(description);
            article.setHash(UUID.randomUUID().toString());
            article.setUser(user);
            articleDao.save(article);
        }

        Page<Article> all = articleDao.findAll(PageRequest.of(0, 4, Sort.by(Sort.Order.desc("id"))));
        model.addAttribute("articles", all);

        return "editor";
    }

}
