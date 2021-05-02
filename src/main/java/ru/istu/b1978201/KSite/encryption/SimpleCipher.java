package ru.istu.b1978201.KSite.encryption;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SimpleCipher {

    private static final String PRIVATE_KEY_BASE64 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDYzfkSmEqLUBw1tEa1P8htB2Ts+PW3n66qELECPujBmS900+hEcI8iDeLpmUhnN2VOXvOBxzCqqPJCGGucGlUCU88tJCCdSqxxRzZxdfigU6Rq3wOHEAydYgVb9eHxA9Na5yxG0X27Ah38FkaAlSGPnxvx29EuoCGYkgBLe7MVkXodMtb4PYWRzQgEuFQbeYebN72aV+KKipTFzygqkwh22hT7rsFyqqJASUJlM6N7yHZcvvEzZoGLURnf0pezrlsgFUR6EXxTWj1JgjG/pHs2V5CCCPGCgnIQWNwpUpWPpk7GK5UT/GL04WueXZ0n1lJp+M1+7VIXQo26QpaOA3q/AgMBAAECggEBAKi5a1xwTpsqxjl5SgnmUJmhRocxnTDBQdIyWT8xLDz2dTtLaxhQLORIwE4hr3o8X0fll0ZpeVNnVdngXWbIC15bUQGFeyhOI/OogxM7xzkmvu85Deq/r8GPtjICIktKOeMlw4x9noa6OBGmWEnbRZobLklEVAAgOOUewsH7y7w844XhtmLDxEw6jTluPhScdiPZnxBEB0AdreEx7MT61ZXUiblc+8JpFmIDZ77ENab3c+JMsigYgnzHE8DBDTQ9zb2iJsKmUucj+eLTqVg2hBTWcinSM/LljSL5ONxg1vHXDaH7J+z4VWQWTbRoqkSPmZ4k7aAr+5EryjajNb9lgqkCgYEA7BwOCjiglquJ0EVf2rXqd17WLg5B1bQqsoP7U99C7JsXSVVhYVBFOtJrFvtzf8J/ax705GsojbbQ6MSeybbaB/jUU4fBOQgADUsM1jTIOOC22YjMkUsjrkxIzTu8+FJg8x8uVgv5J2De2FOFE/anW7QHrGiMujZFaxyq2dCy75sCgYEA6xGV+VKWd8+vu6eygMfmRnU7JLJXxoQfmLMiaCN2KZPxqHWvyJbcKC9xl9B3SssQLk1xC5GNoZEvFQOWZOlvSom6ZYQb7HkULth1oLWM4yLs0UsjnysClwLT0ocU30uLvdCYazISuwV7sotG4xoYZBZ8U3NgihR66tGbEXKJHa0CgYBS2PVEOOKAe+h7oBwmTjbbc/eLaY1IQ1PIF++UWn7NLw5YuRDWaKgiH7tb8Rdx+PlkyRP9Imy0bTWYt4VQGk0fBvzZ4oS2UkxSXcc/Un3/FzqR0iKVyNvYSQQyn4eLSryiEorbvo/KdX88QPItMxs+XkljAbx/ipOJJiH9E/H1iQKBgQCmURX8houBj8d1jUxFw6nQ1icXaruh8F9uqefBKL304wELQOEzmlGAeTj0wgF1QMFdEVvXZI+B7fgS9SvWkDc21MW2B8e+A1jdYDexkmJxXoeR+YjlmAfIEq8OeJay+n+PuGPvDEmmW1XO2K46Bn+VaTdjz/FRIPZq6AzARnpvLQKBgBFKRkMTV2ROjgBjbbqZMlzcIUZev1OSAvnyJx3Ot4uBN/stI+8x9OnOq7qenAIrvawQrmwJ738KOZRViJsqHdHemdepoKbRnDEcdRyUzqPc2TsGuJGmCUnkvyTqUUaRTLOVQbrqc0efFRR3fUbMmhkO+fxgq5LWAfB7t55toJ3X";
    private static final String PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2M35EphKi1AcNbRGtT/IbQdk7Pj1t5+uqhCxAj7owZkvdNPoRHCPIg3i6ZlIZzdlTl7zgccwqqjyQhhrnBpVAlPPLSQgnUqscUc2cXX4oFOkat8DhxAMnWIFW/Xh8QPTWucsRtF9uwId/BZGgJUhj58b8dvRLqAhmJIAS3uzFZF6HTLW+D2Fkc0IBLhUG3mHmze9mlfiioqUxc8oKpMIdtoU+67BcqqiQElCZTOje8h2XL7xM2aBi1EZ39KXs65bIBVEehF8U1o9SYIxv6R7NleQggjxgoJyEFjcKVKVj6ZOxiuVE/xi9OFrnl2dJ9ZSafjNfu1SF0KNukKWjgN6vwIDAQAB";

    private static Cipher cipherDecrypt;
    private static Cipher cipherEncrypt;

    private static Signature encoderSignature;
    private static Signature verifySignature;

    static {
        try {
            cipherDecrypt = Cipher.getInstance("RSA");
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(PRIVATE_KEY_BASE64.getBytes())));
            cipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey);

            cipherEncrypt = Cipher.getInstance("RSA");
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY_BASE64.getBytes())));
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);

            encoderSignature = Signature.getInstance("SHA256withRSA");
            encoderSignature.initSign(privateKey);

            verifySignature = Signature.getInstance("SHA256withRSA");
            verifySignature.initVerify(publicKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public static byte[] decode(byte[] data) throws BadPaddingException, IllegalBlockSizeException {
        return cipherDecrypt.doFinal(data);
    }

    public static byte[] encode(byte[] data) throws BadPaddingException, IllegalBlockSizeException {
        return cipherEncrypt.doFinal(data);
    }

    public static byte[] encodeSignature(byte[] data) throws SignatureException {
        encoderSignature.update(data);
        return encoderSignature.sign();
    }
    public static boolean verifySignature(byte[] signature, byte[] expectedSignature) throws SignatureException {
        verifySignature.update(signature);
        return verifySignature.verify(expectedSignature);
    }

}
