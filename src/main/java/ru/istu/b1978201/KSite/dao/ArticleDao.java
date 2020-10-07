package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.Article;

import java.util.List;

@Component
public interface ArticleDao extends JpaRepository<Article, Long> {

    Article findByHash(String hash);
    List<Article> findAllByUserId(long id);

}
