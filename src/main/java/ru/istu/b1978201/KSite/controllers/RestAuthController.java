package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.istu.b1978201.KSite.encryption.JWT;
import ru.istu.b1978201.KSite.mode.AuthToken;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.*;
import ru.istu.b1978201.KSite.utils.AuthStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class RestAuthController {

    @Autowired
    public UserService userService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private JWT jwt;

    @Autowired
    private BanedAccessTokenService banedAccessTokenService;
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private AllowedServicesService allowedServicesService;

    @Autowired
    private UtilService utilService;

    @PostMapping(value = {"api/refresh"})
    public Map<String, Object> authWithRefreshToken(HttpServletRequest requestS, @RequestParam(value = "id", defaultValue = "") long id,
                                                    @RequestParam(value = "refresh_token", defaultValue = "") String token,
                                                    @RequestParam(value = "device_id", defaultValue = "") String deviceId,
                                                    @RequestParam(value = "service_id", defaultValue = "") String serviceId) {
        Map<String, Object> json = new HashMap<>();


        Map<String, String> parameters = new HashMap<>();
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            parameters.put(par[0], par[1]);
        }

        token = parameters.getOrDefault("refresh_token", null);
        String deviceInParameters = parameters.getOrDefault("device_id", "");
        deviceId = deviceInParameters.isEmpty()?null:new String(Base64.getDecoder().decode(deviceInParameters.getBytes()));
        if (deviceId!=null && allowedServicesService.allowedService(serviceId).isPresent()) {
            try {
                JSONObject deviceDataJSON = new JSONObject(deviceId);
                if (deviceDataJSON.has("ip") && deviceDataJSON.has("id")) {
                    String device = deviceDataJSON.getString("id");

                    User user = userService.findById(id);
                    json.put("auth_status", AuthStatus.INVALID_INPUT_DATA);
                    if (user != null) {
                        json.put("auth_status", AuthStatus.ERROR);
                        if (device != null && !device.isEmpty()) {
                            Optional<AuthToken> optionalAuthToken = authTokenService.findAuthToken(user, serviceId, device);
                            if (optionalAuthToken.isPresent()) {
                                AuthToken authToken = optionalAuthToken.get();
                                if (authToken.getRefreshToken().equals(token)) {
                                    Optional<JSONObject> aliveToken = jwt.isAlive(authToken.getRefreshToken(), device);
                                    if (aliveToken.isPresent()) {
                                        utilService.instanceData(json, user, serviceId, device);
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

   /* @RequestMapping(value = {"api/auth"})
    public Map<String, Object> auth(HttpServletRequest requestS,
                                    @RequestParam(value = "login", defaultValue = "") String login,
                                    @RequestParam(value = "password", defaultValue = "") String password,
                                    @RequestParam(value = "device_id", defaultValue = "") String deviceId
    ) {


        System.out.println("TEST2");

        Map<String, Object> json = new HashMap<>();

        String serviceId = "1";

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
    }*/

    @PostMapping(value = {"api/logout}"})
    public Map<String, Object> logout(@RequestParam(value = "service_id", defaultValue = "") String serviceId,
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




}
