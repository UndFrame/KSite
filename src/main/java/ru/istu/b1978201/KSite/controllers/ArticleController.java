package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.CommentDao;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.Comment;

import java.util.UUID;

@Controller
public class ArticleController {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private CommentDao commentDao;

    @GetMapping("article")
    public String getArticle(@ModelAttribute("id") String id, Model model) {
        if(id.isEmpty()){

            Page<Article> all = articleDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"))));

            model.addAttribute("articles",all);

            return "articlelist";
        }

        Article article = articleDao.findByHash(id);

        model.addAttribute("findArticle",article!=null);
        model.addAttribute("article",article);
        if(article!=null){
            model.addAttribute("comments",article.getComment());
        }

        return "article";
    }

    @PostMapping("article")
    public String addComment(@ModelAttribute("id") String id,@ModelAttribute("comment") String comment, Model model) {
        if(id.isEmpty()){

            Page<Article> all = articleDao.findAll(PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"))));
            model.addAttribute("articles",all);

            return "articlelist";
        }

        Article article = articleDao.findByHash(id);
        model.addAttribute("findArticle",article!=null);
        model.addAttribute("article",article);


        if(article!=null){
            Comment newComment = new Comment();
            newComment.setComment(comment);
            newComment.setArticle(article);

            article.getComment().add(newComment);
            commentDao.save(newComment);
            model.addAttribute("comments",article.getComment());

        }

        return "article";
    }

    @GetMapping("editor")
    public String getEditor(Model model){
        Page<Article> all = articleDao.findAll(PageRequest.of(0, 4, Sort.by(Sort.Order.desc("id"))));
        model.addAttribute("articles",all);
        return "editor";
    }


    @PostMapping("editor")
    public String getEditor(@ModelAttribute("description") String description, @ModelAttribute("text") String text, Model model) {

        Article article = new Article();
        article.setText(text);
        article.setDescription(description);
        article.setHash(UUID.randomUUID().toString());

        Comment e = new Comment();
        e.setComment("YOU LOX");
        e.setArticle(article);
        article.getComment().add(e);

        Comment e1 = new Comment();
        e1.setComment("YOU LOX2");
        e1.setArticle(article);
        article.getComment().add(e1);

        commentDao.save(e);
        commentDao.save(e1);
        articleDao.save(article);


        Page<Article> all = articleDao.findAll(PageRequest.of(0, 4, Sort.by(Sort.Order.desc("id"))));
        model.addAttribute("articles",all);

        return "editor";
    }

}
