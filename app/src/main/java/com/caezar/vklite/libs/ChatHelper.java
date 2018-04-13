package com.caezar.vklite.libs;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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

    public static void setAvatar(boolean isNonDuplicatesAvatar, RoundedImageView avatarView, String avatarUrl) {
        if (isNonDuplicatesAvatar) {
            asyncImageLoad(avatarUrl, avatarView);
            avatarView.setVisibility(View.VISIBLE);
        } else {
            avatarView.setVisibility(View.INVISIBLE);
        }
    }
}
