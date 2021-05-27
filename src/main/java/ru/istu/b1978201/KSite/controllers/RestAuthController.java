package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping(value = {"api/refresh/{serviceId}"})
    public Map<String, Object> authWithRefreshToken(@PathVariable(value = "serviceId") String serviceId, HttpServletRequest requestS, @RequestParam(value = "id", defaultValue = "") long id,
                                                    @RequestParam(value = "refresh_token", defaultValue = "") String token,
                                                    @RequestParam(value = "device_id", defaultValue = "") String deviceData) {
        Map<String, Object> json = new HashMap<>();

        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            parameters.put(par[0], par[1]);
        }

        token = parameters.getOrDefault("refresh_token", null);

        if (ServicesId.isSupport(serviceId)) {
            try {
                JSONObject deviceDataJSON = new JSONObject(deviceData);
                if (deviceDataJSON.has("ip") && deviceDataJSON.has("id")) {
                    String deviceId = deviceDataJSON.getString("id");

                    User user = userService.findById(id);
                    json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
                    if (user != null) {
                        json.put("auth_status", AuthStatus.ERROR);
                        if (deviceData != null && !deviceData.isEmpty()) {
                            Optional<AuthToken> optionalAuthToken = authTokenService.findAuthToken(user, serviceId, deviceId);
                            if (optionalAuthToken.isPresent()) {
                                AuthToken authToken = optionalAuthToken.get();
                                if (authToken.getRefreshToken().equals(token)) {
                                    if (JWT.isAlive(authToken.getRefreshToken())) {
                                        instanceData(json, user, serviceId, deviceId);
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
                } else {
                    json.put("auth_status", AuthStatus.INVALID_INPUT_DATA_JSON);
                }
            } catch (JSONException e) {
                json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
            }
        } else {
            json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
        }

        return json;
    }

    @PostMapping(value = {"api/auth/{serviceId}"})
    public Map<String, Object> authWithLogin(@PathVariable(value = "serviceId") String serviceId, @RequestParam(value = "login", defaultValue = "") String login,
                                             @RequestParam(value = "password", defaultValue = "") String password,
                                             @RequestParam(value = "device_id", defaultValue = "") String deviceId) {

        Map<String, Object> json = new HashMap<>();

        if (ServicesId.isSupport(serviceId)) {
            try {
                JSONObject deviceDataJSON = new JSONObject(deviceId);
                if (deviceDataJSON.has("ip") && deviceDataJSON.has("id")) {
                    User user = userService.findByUsername(login);
                    if (user == null) {
                        user = userService.findByEmail(login);
                    }
                    json.put("auth_status", AuthStatus.ERROR);
                    if (user != null) {
                        try {
                            if (deviceId != null && !deviceId.isEmpty()) {
                                SecretKeySpec aesKey = new SecretKeySpec(Base64.getDecoder().decode(SimpleCipher.PASSWORD_CIPHER_KEY.getBytes()), "AES");
                                Cipher cipher = Cipher.getInstance("AES");
                                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                                byte[] encrypted = DatatypeConverter.parseBase64Binary(password);
                                String s = new String(cipher.doFinal(encrypted));

                                if (passwordEncoder.matches(s, user.getPassword())) {
                                    instanceData(json, user, serviceId, deviceDataJSON.getString("id"));
                                } else {
                                    json.put("auth_status", AuthStatus.INVALID_PASSWORD);
                                }
                            } else {
                                json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
                            }
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    json.put("auth_status", AuthStatus.INVALID_INPUT_DATA_JSON);
                }
            } catch (JSONException e) {
                json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
            }
        } else {
            json.put("auth_status", AuthStatus.SERVICE_NOT_SUPPORT);
        }


        return json;
    }

    private void instanceData(Map<String, Object> json, User user, String serviceId, String deviceData) {
        AuthToken newToken = authTokenService.getNewToken(user, serviceId, deviceData);

        json.put("id", user.getId());
        json.put("username", user.getUsername());
        json.put("email", user.getEmail());
        json.put("enabled", user.isEnabled());
        json.put("ban", user.isBan());

        json.put("access_token", newToken.getAccessToken());
        json.put("refresh_token", newToken.getRefreshToken());
        authTokenService.save(newToken, true);
        json.put("auth_status", AuthStatus.SUCCESSFUL_AUTHORIZATION);
    }

    @PostMapping
    public Map<String,Object> isAuthorized()

}
