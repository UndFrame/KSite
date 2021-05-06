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

    public AuthToken getNewToken(User user, long serviceId, long device_id) {

        AuthToken authToken = new AuthToken();

        authToken.setAccessToken(JWT.getToken(user, false));
        authToken.setRefreshToken(JWT.getToken(user, true));
        authToken.setUserId(user.getId());
        authToken.setServiceId(serviceId);
        authToken.setDeviceId(device_id);

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

        public Optional<AuthToken> findAuthToken(User user, long serviceId, long device_id) {

        if (user == null) {
            return Optional.empty();
        }
        return findAuthToken(user.getId(), serviceId, device_id);
    }

    public Optional<AuthToken> findAuthToken(long userId, long serviceId, long device_id) {
        return Optional.ofNullable(authTokenDao.findFirstByUserIdAndServiceIdAndDeviceId(userId, serviceId, device_id));
    }

}
