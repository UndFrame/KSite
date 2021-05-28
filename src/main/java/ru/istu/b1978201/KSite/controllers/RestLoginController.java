package ru.istu.b1978201.KSite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.istu.b1978201.KSite.encryption.SimpleCipher;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.AllowedServicesService;
import ru.istu.b1978201.KSite.services.UserService;
import ru.istu.b1978201.KSite.services.UtilService;
import ru.istu.b1978201.KSite.utils.ResponseStatus;

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

@RestController
public class RestLoginController {

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private AllowedServicesService allowedServicesService;
    @Autowired
    public UserService userService;
    @Autowired
    private UtilService utilService;

    @PostMapping(value = {"api/auth"})
    public Map<String, Object> auth(HttpServletRequest requestS,
                                    @RequestParam(value = "login", defaultValue = "") String login,
                                    @RequestParam(value = "password", defaultValue = "") String password,
                                    @RequestParam(value = "device_id", defaultValue = "") String deviceId,
                                    @RequestParam(value = "service_id", defaultValue = "") String serviceId
    ) {
        Map<String, Object> json = new HashMap<>();


        Map<String, String> parameters = new HashMap<>();
        if(requestS.getQueryString()!=null)
            for (String parameter : requestS.getQueryString().split("&")) {
            String[] par = parameter.split("=", 2);
            parameters.put(par[0], par[1]);
        }
        String device = parameters.getOrDefault("device_id", "");
        deviceId = device.isEmpty()? null:new String(Base64.getDecoder().decode(device.getBytes()));


        if (allowedServicesService.allowedService(serviceId).isPresent()) {
            try {
                JSONObject deviceDataJSON = new JSONObject(deviceId);
                if (deviceDataJSON.has("ip") && deviceDataJSON.has("id")) {
                    User user = userService.findByUsername(login);
                    if (user == null) {
                        user = userService.findByEmail(login);
                    }
                    json.put("auth_status", ResponseStatus.ERROR);
                    if (user != null) {
                        try {
                            if (deviceId != null && !deviceId.isEmpty()) {
                                SecretKeySpec aesKey = new SecretKeySpec(Base64.getDecoder().decode(SimpleCipher.PASSWORD_CIPHER_KEY.getBytes()), "AES");
                                Cipher cipher = Cipher.getInstance("AES");
                                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                                byte[] encrypted = DatatypeConverter.parseBase64Binary(password);
                                String s = new String(cipher.doFinal(encrypted));
                                if (passwordEncoder.matches(s, user.getPassword())) {
                                    utilService.instanceData(json, user, serviceId, deviceDataJSON.getString("id"));
                                } else {
                                    json.put("auth_status", ResponseStatus.INVALID_PASSWORD);
                                }
                            } else {
                                json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
                            }
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA_JSON);
                }
            } catch (JSONException e) {
                json.put("auth_status", ResponseStatus.INVALID_INPUT_DATA);
            }
        } else {
            json.put("auth_status", ResponseStatus.SERVICE_NOT_SUPPORT);
        }


        return json;

    }

}
