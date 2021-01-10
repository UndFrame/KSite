package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.LikeDislike;
/**
 * Интерфейс обеспечивает основные операции по поиску, сохранения, удалению данных из таблицы like_dislike.
 */
@Component
public interface LikeDislikeDao extends JpaRepository<LikeDislike, Long> {
}
