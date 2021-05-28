package ru.istu.b1978201.KSite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.mode.AuthToken;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.utils.ResponseStatus;

import java.util.Map;

@Service
public class UtilService {


    @Autowired
    private AuthTokenService authTokenService;


    public void instanceData(Map<String, Object> json, User user, String serviceId, String deviceId) {
        AuthToken newToken = authTokenService.getNewToken(user, serviceId, deviceId);

        json.put("id", user.getId());
        json.put("username", user.getUsername());
        json.put("email", user.getEmail());
        json.put("enabled", user.isEnabled());
        json.put("ban", user.isBan());

        json.put("access_token", newToken.getAccessToken());
        json.put("refresh_token", newToken.getRefreshToken());
        authTokenService.save(newToken, true);
        json.put("auth_status", ResponseStatus.SUCCESSFUL_AUTHORIZATION);
    }

}
