package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.Article;

import java.util.List;
/**
 * Интерфейс обеспечивает основные операции по поиску, сохранения, удалению данных из таблицы articles.
 */
@Component
public interface ArticleDao extends JpaRepository<Article, Long> {

    Article findByHash(String hash);
    List<Article> findAllByUserId(long id);
    void deleteById(long ld);

}
