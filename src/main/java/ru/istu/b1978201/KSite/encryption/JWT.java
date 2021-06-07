package ru.istu.b1978201.KSite.encryption;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.istu.b1978201.KSite.mode.User;
import ru.istu.b1978201.KSite.services.BanedAccessTokenService;

import java.security.SignatureException;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
public class JWT {

    @Autowired
    private BanedAccessTokenService banedAccessTokenService;

    private Random random = new Random();

    public String getToken(User user,String deviceId,boolean refresh) {
        String accessToken = "";
        try {
            JSONObject headerJson = new JSONObject();
            headerJson.put("alg", "RS256");
            headerJson.put("typ", "JWT");

            JSONObject payloadJson = new JSONObject();
            payloadJson.put("exp", (System.currentTimeMillis() / 1000L) + (refresh ? 60 * 60 * 24 * 7 : 60*15));
            payloadJson.put("uid", user.getId());
            payloadJson.put("un", user.getUsername());
            payloadJson.put("did", deviceId);

            payloadJson.put("id", random.nextLong());

            String hS = headerJson.toString();
            String pS = payloadJson.toString();
            String header = base64EncodeSB(hS.getBytes());
            String payload = base64EncodeSB(pS.getBytes());


            String signature = base64EncodeSB(SimpleCipher.encodeSignature((header + "." + payload).getBytes()));
            accessToken = header + "." + payload + "." + signature;
        } catch (JSONException | SignatureException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public Optional<JSONObject> isAlive(String token) {
        return this.isAlive(token, null);
    }

    public Optional<JSONObject> isAlive(String token,String deviceId) {
        String[] split = token.split("\\.");
        if (split.length == 3) {
            try {
                String header = split[0];
                String payload = split[1];
                String signatureExpected = split[2];
                JSONObject payloadJSON = new JSONObject(base64DecodeSS(payload));
                long time = payloadJSON.getLong("exp");

                boolean isAliveToken = SimpleCipher.verifySignature(
                        (header + "." + payload).getBytes(), base64DecodeBB(signatureExpected.getBytes())) && (time >= (System.currentTimeMillis() / 1000L));

                if(isAliveToken){
                    long userId = payloadJSON.getLong("uid");
                    if(banedAccessTokenService.isBanned(userId, token))
                        return Optional.empty();
                }

                if(deviceId!=null && !deviceId.equals(payloadJSON.getString("did"))){
                    return Optional.empty();
                }

                return isAliveToken ?
                        Optional.of(payloadJSON) :
                        Optional.empty();


            } catch (SignatureException | JSONException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public String base64EncodeSS(String s){
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    public String base64EncodeSB(byte[] b){
        return Base64.getEncoder().encodeToString(b);
    }

    public String base64DecodeSS(String s){
        return new String(Base64.getDecoder().decode(s.getBytes()));
    }

    public byte[] base64DecodeBS(String s){
        return Base64.getDecoder().decode(s.getBytes());
    }
    public byte[] base64DecodeBB(byte[] b){
        return Base64.getDecoder().decode(b);
    }

    public String base64DecodeSB(byte[] bytes) {
        return new String(Base64.getDecoder().decode(bytes));
    }

   /* public static Optional<Long> getUserId(String token) {
        String[] split = token.split("\\.");
        if (split.length == 3) {
            try {
                JSONObject payloadJSON = new JSONObject(new String(Base64.getDecoder().decode(split[1].getBytes())));
                return Optional.ofNullable(payloadJSON.has("userId") ? payloadJSON.getLong("userId") : null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }*/

}
