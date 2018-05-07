package com.caezar.vklite.helpers;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.caezar.vklite.R;
import com.caezar.vklite.models.network.DialogMessage;
import com.caezar.vklite.models.network.Photo;
import com.makeramen.roundedimageview.RoundedImageView;

import static com.caezar.vklite.libs.ImageLoader.asyncImageLoad;

/**
 * Created by seva on 13.04.18 in 16:07.
 */

public class ChatHelper {
    public static boolean isNonDuplicatesAvatar(int itemSize, int position, int userId, int prevUserId, int nextUserId, boolean scrollUp) {
        final boolean isLastItem = (itemSize - 1) == position;
        final boolean isMessageSameAuthorBelow = userId != prevUserId && !scrollUp;
        final boolean isMessageSameAuthorUp = nextUserId != userId && scrollUp;
        return isLastItem || isMessageSameAuthorBelow || isMessageSameAuthorUp;
    }

    public static void setAlignLayoutRight(RelativeLayout container) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        container.setLayoutParams(params);
    }

    public static void swapButtonsVisibility(Button button1, Button button2) {
        int firstVisibility = button1.getVisibility();
        int secondVisibility = button2.getVisibility();
        button1.setVisibility(secondVisibility);
        button2.setVisibility(firstVisibility);
    }

    public static void setAvatar(boolean isNonDuplicatesAvatar, RoundedImageView avatarView, String avatarUrl) {
        if (isNonDuplicatesAvatar) {
            asyncImageLoad(avatarUrl, avatarView);
            avatarView.setVisibility(View.VISIBLE);
        } else {
            avatarView.setVisibility(View.INVISIBLE);
        }
    }

    public static void unsetAlignLayoutRight(RelativeLayout container) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
        params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        container.setLayoutParams(params);
    }

    public static void unsetAvatar(RoundedImageView avatarView) {
        avatarView.setVisibility(View.GONE);
    }

    public static String getMessageImageUrl(DialogMessage item) {
        return item.getAttachments()[0].getPhoto().getPhoto_604();
    }

    public static String getMessageImageMax(DialogMessage item) {
        Photo photo = item.getAttachments()[0].getPhoto();
        String maxPhotoSize = photo.getPhoto_2560();
        if (maxPhotoSize == null) {
            maxPhotoSize = photo.getPhoto_1280();
        }
        if (maxPhotoSize == null) {
            maxPhotoSize = photo.getPhoto_807();
        }
        if (maxPhotoSize == null) {
            maxPhotoSize = photo.getPhoto_604();
        }
        System.out.println(maxPhotoSize);
        return maxPhotoSize;
    }

    public static String getMessageStickerUrl(DialogMessage item) {
        return item.getAttachments()[0].getSticker().getImages()[2].getUrl();
    }

    public static String getDocSize(long byteSize, Context context) {
        final double oneKilobyte = 1024.0;

        double kilobytes = byteSize / Math.pow(oneKilobyte, 1);
        double megabytes = byteSize / Math.pow(oneKilobyte, 2);
        double gigabytes = byteSize / Math.pow(oneKilobyte, 3);
        double terabytes = byteSize / Math.pow(oneKilobyte, 4);

        if (terabytes > 1) {
            return sizeToString(terabytes).concat(context.getString(R.string.terabytes));
        }

        if (gigabytes > 1) {
            return sizeToString(gigabytes).concat(context.getString(R.string.gigabytes));
        }

        if (megabytes > 1) {
            return sizeToString(megabytes).concat(context.getString(R.string.megabytes));
        }

        if (kilobytes > 1) {
            return sizeToString(kilobytes).concat(context.getString(R.string.kilobytes));
        }

        return sizeToString((double) byteSize).concat(context.getString(R.string.bytes));
    }

    private static String sizeToString(double size) {
        return String.valueOf((int) size).concat(" ");
    }
}
