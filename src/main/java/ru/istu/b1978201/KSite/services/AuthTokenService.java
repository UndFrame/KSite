package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.dao.AuthTokenDao;
import ru.istu.b1978201.KSite.encryption.JWT;
import ru.istu.b1978201.KSite.mode.AuthToken;
import ru.istu.b1978201.KSite.mode.User;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthTokenService {

    @Autowired
    private AuthTokenDao authTokenDao;

    @Autowired
    private JWT jwt;

    public AuthToken getNewToken(User user, String serviceId, String deviceId) {

        AuthToken authToken = new AuthToken();

        authToken.setAccessToken(jwt.getToken(user,deviceId, false));
        authToken.setRefreshToken(jwt.getToken(user,deviceId, true));
        authToken.setUserId(user.getId());
        authToken.setServiceId(serviceId);
        authToken.setDeviceId(deviceId);

        return authToken;
    }

    public void save(AuthToken authToken,boolean update) {
        if(update)
        findAuthToken(authToken).ifPresent(token->{
            authToken.setId(token.getId());
        });
        authTokenDao.save(authToken);
    }

    public Optional<AuthToken> findAuthToken(AuthToken authToken) {
        Objects.requireNonNull(authToken);
        return findAuthToken(authToken.getUserId(), authToken.getServiceId(), authToken.getDeviceId());
    }

        public Optional<AuthToken> findAuthToken(User user, String serviceId, String deviceData) {

        if (user == null) {
            return Optional.empty();
        }
        return findAuthToken(user.getId(), serviceId, deviceData);
    }

    public Optional<AuthToken> findAuthToken(long userId, String serviceId, String deviceData) {
        return Optional.ofNullable(authTokenDao.findFirstByUserIdAndServiceIdAndDeviceId(userId, serviceId, deviceData));
    }


    public void logOut(long userId){
        authTokenDao.deleteAllByUserId(userId);
    }
    public void logOut(long userId,String serviceId){
        authTokenDao.deleteAllByUserIdAndServiceId(userId,serviceId);
    }

    public void logOut(long userId,String serviceId,String deviceId){
        authTokenDao.deleteAllByUserIdAndServiceIdAndDeviceId(userId,serviceId,deviceId);
    }

    public boolean isAuthorized(String accessToken){
        return jwt.isAlive(accessToken).isPresent();
    }

}
