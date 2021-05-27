package ru.istu.b1978201.KSite.encryption;


import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import ru.istu.b1978201.KSite.mode.User;

import java.security.SignatureException;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

public class JWT {

    private static Random random = new Random();

    public static String getToken(User user,String deviceId,boolean refresh) {
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
            String header = new String(Base64.getEncoder().encode(hS.getBytes()));
            String payload = new String(Base64.getEncoder().encode(pS.getBytes()));


            String signature = new String(Base64.getEncoder().encode(SimpleCipher.encodeSignature((header + "." + payload).getBytes())));
            accessToken = header + "." + payload + "." + signature;
        } catch (JSONException | SignatureException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public static Optional<JSONObject> isAlive(String token) {
        String[] split = token.split("\\.");
        if (split.length == 3) {
            try {
                String header = split[0];
                String payload = split[1];
                String signatureExpected = split[2];
                JSONObject payloadJSON = new JSONObject(new String(Base64.getDecoder().decode(payload)));
                long time = payloadJSON.getLong("exp");

                return (SimpleCipher.verifySignature(
                        (header + "." + payload).getBytes(), Base64.getDecoder().decode(signatureExpected.getBytes())) && (time >= (System.currentTimeMillis() / 1000L))) ?
                        Optional.of(payloadJSON) :
                        Optional.empty();


            } catch (SignatureException | JSONException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
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
