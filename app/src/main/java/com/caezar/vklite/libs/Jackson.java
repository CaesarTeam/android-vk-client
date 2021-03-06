package com.caezar.vklite.libs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by seva on 09.04.18 in 19:59.
 */

public class Jackson {
    private static final ObjectMapper INSTANCE = new ObjectMapper();

    public static void configureInstance() {
        INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        INSTANCE.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }

    static public <T> T parseBody(Class<T> clazz, String body) {
        T model = null;
        try {
            model = INSTANCE.readValue(body, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }
}
