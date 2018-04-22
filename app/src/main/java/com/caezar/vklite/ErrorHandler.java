package com.caezar.vklite;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.caezar.vklite.models.network.response.ErrorVkApiResponse;

import static com.caezar.vklite.libs.ParseResponse.parseBody;

/**
 * Created by seva on 07.04.18 in 0:30.
 */

public class ErrorHandler {

    private static int errorParse(String body) {
        ErrorVkApiResponse errorVkApiResponse = parseBody(ErrorVkApiResponse.class, body);

        if (errorVkApiResponse != null) {
            switch (errorVkApiResponse.getError().getError_code()) {
                case 1:
                    return R.string.error_API_Vk_1;
                case 2:
                    return R.string.error_API_Vk_2;
                case 3:
                    return R.string.error_API_Vk_3;
                case 4:
                    return R.string.error_API_Vk_4;
                case 5:
                    return R.string.error_API_Vk_5;
                case 6:
                    return R.string.error_API_Vk_6;
                case 7:
                    return R.string.error_API_Vk_7;
                case 8:
                    return R.string.error_API_Vk_8;
                case 9:
                    return R.string.error_API_Vk_9;
                case 10:
                    return R.string.error_API_Vk_10;
                case 11:
                    return R.string.error_API_Vk_11;
                case 14:
                    return R.string.error_API_Vk_14;
                case 15:
                    return R.string.error_API_Vk_15;
                case 16:
                    return R.string.error_API_Vk_16;
                case 17:
                    return R.string.error_API_Vk_17;
                case 18:
                    return R.string.error_API_Vk_18;
                case 20:
                    return R.string.error_API_Vk_20;
                case 21:
                    return R.string.error_API_Vk_21;
                case 23:
                    return R.string.error_API_Vk_23;
                case 24:
                    return R.string.error_API_Vk_24;
                case 27:
                    return R.string.error_API_Vk_27;
                case 28:
                    return R.string.error_API_Vk_28;
                case 29:
                    return R.string.error_API_Vk_29;
                case 100:
                    return R.string.error_API_Vk_100;
                case 101:
                    return R.string.error_API_Vk_101;
                case 113:
                    return R.string.error_API_Vk_113;
                case 150:
                    return R.string.error_API_Vk_150;
                case 200:
                    return R.string.error_API_Vk_200;
                case 201:
                    return R.string.error_API_Vk_201;
                case 203:
                    return R.string.error_API_Vk_203;
                case 300:
                    return R.string.error_API_Vk_300;
                case 500:
                    return R.string.error_API_Vk_500;
                case 600:
                    return R.string.error_API_Vk_600;
                case 603:
                    return R.string.error_API_Vk_603;
            }
        }

        return -1;
    }

    public static void makeToastError(String body, final Context context) {
        final int stringRes = errorParse(body);
        if (stringRes != -1) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show());
        }
    }

    public static void makeToastError(String body, final Activity activity) {
        final int stringRes = errorParse(body);
        if (stringRes != -1) {
            activity.runOnUiThread(() -> Toast.makeText(activity, stringRes, Toast.LENGTH_SHORT).show());
        }
    }

    public static void createErrorInternetToast(final Context context) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, R.string.error_internet_connection, Toast.LENGTH_SHORT).show());
    }

    public static void createErrorInternetToast(final Activity activity) {
        activity.runOnUiThread(() -> Toast.makeText(activity, R.string.error_internet_connection, Toast.LENGTH_SHORT).show());
    }
}
