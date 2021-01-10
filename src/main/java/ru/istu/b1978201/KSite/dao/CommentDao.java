package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.Comment;
import ru.istu.b1978201.KSite.mode.Token;
import ru.istu.b1978201.KSite.mode.User;
/**
 * Интерфейс обеспечивает основные операции по поиску, сохранения, удалению данных из таблицы comments.
 */
@Component
public interface CommentDao extends JpaRepository<Comment, Long> {

    void deleteByArticleId(long id);

}
