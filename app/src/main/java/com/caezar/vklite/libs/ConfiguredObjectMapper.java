package com.caezar.vklite.libs;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by seva on 09.04.18 in 19:59.
 */

public class ConfiguredObjectMapper {
    private static final ObjectMapper INSTANCE = new ObjectMapper();

    public static void initInstance() {
        INSTANCE.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }
}
