package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.Article;
import ru.istu.b1978201.KSite.mode.AuthToken;

import java.util.List;

/**
 * Интерфейс обеспечивает основные операции по поиску, сохранения, удалению данных из таблицы articles.
 */
@Component
public interface AuthTokenDao extends JpaRepository<AuthToken, Long> {

    AuthToken findFirstByUserIdAndServiceIdAndDeviceId(long userId, long serviceId,long deviceId);



}
