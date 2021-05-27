package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.istu.b1978201.KSite.encryption.JWT;
import ru.istu.b1978201.KSite.encryption.SimpleCipher;
import ru.istu.b1978201.KSite.mode.AuthToken;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.AllowedServicesService;
import ru.istu.b1978201.KSite.services.AuthTokenService;
import ru.istu.b1978201.KSite.services.BanedAccessTokenService;
import ru.istu.b1978201.KSite.services.UserService;
import ru.istu.b1978201.KSite.utils.AuthStatus;
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

    @Autowired
    private JWT jwt;

    @Autowired
    private BanedAccessTokenService banedAccessTokenService;

    @Autowired
    private AllowedServicesService allowedServicesService;

    @RequestMapping(value = {"api/refresh"})
    public Map<String, Object> authWithRefreshToken(HttpServletRequest requestS, @RequestParam(value = "id", defaultValue = "") long id,
                                                    @RequestParam(value = "refresh_token", defaultValue = "") String token,
                                                    @RequestParam(value = "device_id", defaultValue = "") String deviceId) {

        System.out.println("TEST1");
        Map<String, Object> json = new HashMap<>();


        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            parameters.put(par[0], par[1]);
        }

        token = parameters.getOrDefault("refresh_token", null);
        deviceId = new String(Base64.getDecoder().decode(parameters.getOrDefault("device_id", "").getBytes()));
        if (allowedServicesService.allowedService(0).isPresent()) {
            try {
                JSONObject deviceDataJSON = new JSONObject(deviceId);
                if (deviceDataJSON.has("ip") && deviceDataJSON.has("id")) {
                    String device = deviceDataJSON.getString("id");

                    User user = userService.findById(id);
                    json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
                    if (user != null) {
                        json.put("auth_status", AuthStatus.ERROR);
                        if (device != null && !device.isEmpty()) {
                            Optional<AuthToken> optionalAuthToken = authTokenService.findAuthToken(user, "0", device);
                            if (optionalAuthToken.isPresent()) {
                                AuthToken authToken = optionalAuthToken.get();
                                if (authToken.getRefreshToken().equals(token)) {
                                    Optional<JSONObject> aliveToken = jwt.isAlive(authToken.getRefreshToken(),device);
                                    if (aliveToken.isPresent()) {
                                        instanceData(json, user, "0", device);
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

    @PostMapping(value = {"api/auth"})
    public Map<String, Object> authWithLogin(@RequestParam(value = "serviceId",defaultValue = "") String serviceId, @RequestParam(value = "login", defaultValue = "") String login,
                                             @RequestParam(value = "password", defaultValue = "") String password,
                                             @RequestParam(value = "device_id", defaultValue = "") String deviceId) {


        System.out.println("TEST2");

        Map<String, Object> json = new HashMap<>();

        if (allowedServicesService.allowedService(serviceId).isPresent()) {
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

    @PostMapping(value = {"api/logout}"})
    public Map<String, Object> logout(@RequestParam(value = "serviceId",defaultValue = "") String serviceId,
                                             HttpServletRequest requestS,
                                             @RequestParam(value = "id", defaultValue = "") long id,
                                             @RequestParam(value = "access_token", defaultValue = "") String token) {

        Map<String, Object> json = new HashMap<>();

        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            parameters.put(par[0], par[1]);
        }

        token = parameters.getOrDefault("refresh_token", null);

        if (token != null && allowedServicesService.allowedService(serviceId).isPresent()) {
            try {
                Optional<JSONObject> aliveToken = jwt.isAlive(token);
                if (aliveToken.isPresent()) {
                    banedAccessTokenService.ban(aliveToken.get().getLong("uid"), token);
                }
            } catch (JSONException e) {
                json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
            }
        } else {
            json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
        }

        return json;
    }

    private void instanceData(Map<String, Object> json, User user, String serviceId, String deviceId) {
        AuthToken newToken = authTokenService.getNewToken(user, serviceId, deviceId);

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


}
