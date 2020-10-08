package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.LikeDislike;
import ru.istu.b1978201.KSite.mode.Token;
import ru.istu.b1978201.KSite.mode.User;

@Component
public interface LikeDislikeDao extends JpaRepository<LikeDislike, Long> {
}
