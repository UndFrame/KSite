package ru.istu.b1978201.KSite.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.istu.b1978201.KSite.mode.AuthToken;
import ru.istu.b1978201.KSite.mode.BanedAccessToken;

import java.util.List;

@Component
public interface BanedAccessTokenDao extends JpaRepository<BanedAccessToken, Long> {

    List<BanedAccessToken> findAllByUser(long userId);

}
