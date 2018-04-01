package com.caezar.vklite.network;

/**
 * Created by seva on 25.03.18 in 22:00.
 */

public class Token {
    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Token.token = token;
    }

    public Token() {
    }
}