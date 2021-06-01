package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.istu.b1978201.KSite.encryption.JWT;
import ru.istu.b1978201.KSite.mode.AuthToken;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.*;
import ru.istu.b1978201.KSite.utils.ResponseStatus;

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
        if(requestS.getQueryString()!=null)
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
                    json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
                    if (user != null) {
                        json.put("auth_status", ResponseStatus.ERROR);
                        if (device != null && !device.isEmpty()) {
                            Optional<AuthToken> optionalAuthToken = authTokenService.findAuthToken(user, serviceId, device);
                            if (optionalAuthToken.isPresent()) {
                                AuthToken authToken = optionalAuthToken.get();
                                if (authToken.getRefreshToken().equals(token)) {
                                    Optional<JSONObject> aliveToken = jwt.isAlive(authToken.getRefreshToken(), device);
                                    if (aliveToken.isPresent()) {
                                        utilService.instanceData(json, user, serviceId, device);
                                    } else {
                                        json.put("auth_status", ResponseStatus.INVALID_TOKEN);
                                    }
                                } else {
                                    json.put("auth_status", ResponseStatus.INVALID_TOKEN);
                                }
                            } else {
                                json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
                            }
                        }
                    }
                } else {
                    json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA_JSON);
                }
            } catch (JSONException e) {
                json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
            }
        } else {
            json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
        }

        return json;
    }


    @PostMapping(value = {"api/logout}"})
    public Map<String, Object> logout(@RequestParam(value = "service_id", defaultValue = "") String serviceId,
                                      HttpServletRequest requestS,
                                      @RequestParam(value = "id", defaultValue = "") long id,
                                      @RequestParam(value = "access_token", defaultValue = "") String token) {

        Map<String, Object> json = new HashMap<>();

        Map<String, String> parameters = new HashMap<>();
        if(requestS.getQueryString()!=null)
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
                    authTokenService.logOut(aliveToken.get().getLong("uid"),serviceId,aliveToken.get().getString("did"));
                    json.put("auth_status", ResponseStatus.LOGOUT_SUCCESSFUL);

                }
            } catch (JSONException e) {
                json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
            }
        } else {
            json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
        }

        return json;
    }

    @PostMapping(value ="api/alivetoken")
    public Map<String, Object> isAliveToke( HttpServletRequest requestS,
                                            @RequestParam(value = "access_token",defaultValue = "") String token){
        Map<String, Object> json = new HashMap<>();

        Map<String, String> parameters = new HashMap<>();
        if(requestS.getQueryString()!=null)
        for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            parameters.put(par[0], par[1]);
        }

        token = parameters.getOrDefault("access_token", null);

        if(token!=null){
            Optional<JSONObject> aliveTokenOptional = jwt.isAlive(token);
            if(aliveTokenOptional.isPresent()){
                json.put("status", ResponseStatus.TOKEN_IS_ALIVE);
            }else
                json.put("status", ResponseStatus.TOKEN_NOT_ALIVE);
        }else{
            json.put("status", ResponseStatus.INVALID_INPUT_DATA);
        }

        return json;
    }



}
