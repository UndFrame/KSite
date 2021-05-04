package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.dao.ArticleDao;
import ru.istu.b1978201.KSite.dao.LikeDislikeDao;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.Evaluation;
import ru.istu.b1978201.KSite.mode.User;

@Service
public class ArticleService {

    @Autowired
    private UserService userService;

    @Autowired
    private LikeDislikeDao likeDislikeDao;

    @Autowired
    private ArticleDao articleDao;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setLikeDislikeDao(LikeDislikeDao likeDislikeDao) {
        this.likeDislikeDao = likeDislikeDao;
    }

    public void setArticleDao(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    public Evaluation likeArticle(User user, Article article) {
        Evaluation likeDislike = null;

        userService.refreshUserLikeDislike(user);

        for (Evaluation dislike : user.getLikeDislikes()) {
            if (dislike.getArticle().equals(article)) {
                likeDislike = dislike;
            }
        }
        boolean newLike = false;

        if (likeDislike == null) {
            likeDislike = new Evaluation();
            newLike = true;
        }

        newLike |= likeDislike.isClear();

        likeDislike.setArticle(article);
        likeDislike.setUser(user);
        if(newLike ){
            article.setLikes(article.getLikes() + 1);
            likeDislike.setLike(true);
        }else if(likeDislike.isLike()){
            article.setLikes(article.getLikes() - 1);
            likeDislike.clear();
        }else if(likeDislike.isDislike()){
            article.setLikes(article.getLikes() + 1);
            article.setDislikes(article.getDislikes() - 1);
            likeDislike.setLike(true);
        }

        article.getLikeDislikes().add(likeDislike);
        user.getLikeDislikes().add(likeDislike);
        likeDislikeDao.save(likeDislike);
        articleDao.save(article);
        return likeDislike;
    }

    public Evaluation dislikeArticle(User user, Article article) {
        Evaluation likeDislike = null;

        userService.refreshUserLikeDislike(user);

        for (Evaluation dislike : user.getLikeDislikes()) {
            if (dislike.getArticle().equals(article)) {
                likeDislike = dislike;
            }
        }
        boolean newLike = false;

        if (likeDislike == null) {
            likeDislike = new Evaluation();
            newLike = true;
        }

        newLike |= likeDislike.isClear();

        likeDislike.setArticle(article);
        likeDislike.setUser(user);


        if(newLike ){
            article.setDislikes(article.getDislikes() + 1);
            likeDislike.setDislike(true);
        }else if(likeDislike.isDislike()){
            article.setDislikes(article.getDislikes() - 1);
            likeDislike.clear();
        }else if(likeDislike.isLike()){
            article.setDislikes(article.getDislikes() + 1);
            article.setLikes(article.getLikes() - 1);
            likeDislike.setLike(false);
        }


        article.getLikeDislikes().add(likeDislike);
        user.getLikeDislikes().add(likeDislike);
        likeDislikeDao.save(likeDislike);
        articleDao.save(article);
        return likeDislike;
    }

}
