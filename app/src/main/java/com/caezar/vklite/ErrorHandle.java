package com.caezar.vklite;

import android.widget.Toast;

import com.caezar.vklite.activities.DialogsActivity;
import com.caezar.vklite.network.models.ErrorVkApi;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by seva on 07.04.18 in 0:30.
 */
public class ErrorHandle {

    public static int errorParse(String body) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeReference<ErrorVkApi> mapError = new TypeReference<ErrorVkApi>() {};
        ErrorVkApi errorVkApi = null;
        try {
            errorVkApi = mapper.readValue(body, mapError);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // todo: add all errors
        switch (Objects.requireNonNull(errorVkApi).getError().getError_code()) {
            case 6:
                return R.string.error6;
        }

        return -1;
    }
}
