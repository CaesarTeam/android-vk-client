package com.caezar.vklite.libs;

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
}
