package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.istu.b1978201.KSite.encryption.JWT;
import ru.istu.b1978201.KSite.encryption.SimpleCipher;
import ru.istu.b1978201.KSite.mode.AuthToken;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.AuthTokenService;
import ru.istu.b1978201.KSite.services.UserService;
import ru.istu.b1978201.KSite.utils.AuthStatus;
import ru.istu.b1978201.KSite.utils.ServicesId;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class RestAuthController {

    @Autowired
    public UserService userService;

    @Autowired
    public PasswordEncoder passwordEncoder;


    @Autowired
    private AuthTokenService authTokenService;

    @PostMapping(value = {"api/refresh"})
    public Map<String, Object> authWithRefreshToken(HttpServletRequest requestS, @RequestParam(value = "id", defaultValue = "") long id,
                                                    @RequestParam(value = "refresh_token", defaultValue = "") String token,
                                                    @RequestParam(value = "device_id", defaultValue = "") String deviceId) {
        Map<String, Object> json = new HashMap<>();

        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=",2);
            parameters.put(par[0], par[1]);
        }

        token = parameters.get("refresh_token");

        User user = userService.findById(id);
        json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
        if (user != null) {
            json.put("auth_status", AuthStatus.ERROR);
            if(deviceId!=null && !deviceId.isEmpty()) {
                long device = Long.parseLong(deviceId);
                long serviceId = ServicesId.MOBILE_APK;
                Optional<AuthToken> optionalAuthToken = authTokenService.findAuthToken(user, serviceId, device);
                if (optionalAuthToken.isPresent()) {
                    AuthToken authToken = optionalAuthToken.get();
                    if (authToken.getRefreshToken().equals(token)) {
                        if (JWT.isAlive(authToken.getRefreshToken())) {
                            instanceData(json, user,serviceId,device);
                        } else {
                            json.put("auth_status", AuthStatus.INVALID_TOKEN);
                        }
                    } else {
                        json.put("auth_status", AuthStatus.INVALID_TOKEN);
                    }
                } else {
                    json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
                }
            }
        }

        return json;
    }

    @PostMapping(value = {"api/auth"})
    public Map<String, Object> authWithLogin(@RequestParam(value = "login", defaultValue = "") String login,
                                       @RequestParam(value = "password", defaultValue = "") String password,
                                             @RequestParam(value = "device_id", defaultValue = "") String deviceId) {

        Map<String, Object> json = new HashMap<>();


        User user = userService.findByUsername(login);
        if (user == null) {
            user = userService.findByEmail(login);
        }
        json.put("auth_status", AuthStatus.ERROR);
        if (user != null) {
            try {

                if(deviceId!=null && !deviceId.isEmpty()) {

                    long device = Long.parseLong(deviceId);

                    SecretKeySpec aesKey = new SecretKeySpec(Base64.getDecoder().decode(SimpleCipher.PASSWORD_CIPHER_KEY.getBytes()), "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, aesKey);
                    byte[] encrypted = DatatypeConverter.parseBase64Binary(password);
                    String s = new String(cipher.doFinal(encrypted));

                    if (passwordEncoder.matches(s, user.getPassword())) {
                        instanceData(json, user, ServicesId.MOBILE_APK, device);
                    } else {
                        json.put("auth_status", AuthStatus.INVALID_PASSWORD);
                    }
                }else{
                    json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
                }
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        }

        return json;
    }

    private void instanceData(Map<String, Object> json, User user,long serviceId,long deviceId) {

        userService.save(user);

        AuthToken newToken = authTokenService.getNewToken(user, serviceId, deviceId);

        json.put("id", user.getId());
        json.put("username", user.getUsername());
        json.put("email", user.getEmail());
        json.put("enabled", user.isEnabled());
        json.put("ban", user.isBan());

        json.put("access_token", newToken.getAccessToken());
        json.put("refresh_token", newToken.getRefreshToken());

        json.put("auth_status", AuthStatus.SUCCESSFUL_AUTHORIZATION);

        authTokenService.save(newToken,true);

    }


}
