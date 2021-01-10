package ru.istu.b1978201.KSite.dao;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleDaoTest {

    @Autowired
    private ArticleDao articleDao;

    private String hash = UUID.randomUUID().toString();

    void save(){

        Article article = new Article();

        article.setLikes(1);
        article.setDislikes(0);
        article.setIcon("bg-6.png");
        article.setHash(hash);
        article.setText("Text");
        article.setDescription("Text");
        User user = new User();
        user.setId(2L);
        article.setUser(user);
        Assert.assertNotNull(articleDao.save(article));
    }

    @Test
    void findByHashAndSave() {
        save();
        Assert.assertNotNull(articleDao.findByHash(hash));
    }



    @Test
    void findAllByUserId() {
        Assert.assertNotNull(articleDao.findAllByUserId(2));
    }

    @Test
    void deleteById() {

        articleDao.findAllByUserId(2).forEach(article -> {
            articleDao.deleteById(article.getId());
        });
        Assert.assertEquals(0, articleDao.findAllByUserId(2).size());
    }
}