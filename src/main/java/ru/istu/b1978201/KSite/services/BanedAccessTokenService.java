package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.dao.BanedAccessTokenDao;
import ru.istu.b1978201.KSite.mode.BanedAccessToken;

@Service
public class BanedAccessTokenService {

    @Autowired
    private BanedAccessTokenDao banedAccessTokenDao;

    public BanedAccessTokenService(){

    }

    public boolean isBanned(long userId, String token) {

        for (BanedAccessToken accessToken : banedAccessTokenDao.findAllByUser(userId)) {
            if(accessToken.getToken().equals(token))
                return true;
        }
        return false;

    }

    public void ban(long id, String token) {

    }
}
