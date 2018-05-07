package com.caezar.vklite.libs;

import android.text.TextUtils;

import com.caezar.vklite.models.network.DialogItem;
import com.caezar.vklite.models.network.Message;
import com.caezar.vklite.models.network.User;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Ints;

import java.util.Collection;
import java.util.List;

import static com.caezar.vklite.helpers.DialogsHelper.getPeerId;


/**
 * Created by seva on 01.05.18 in 14:05.
 */

public class Guava {
    public static int findIndexMessage(List<Message> messages, int messageId) {
        return Iterables.indexOf(messages, message -> message.getId() == messageId);
    }

    public static int findIndexDialog(List<DialogItem> dialogs, int peerId) {
        return Iterables.indexOf(dialogs, dialog -> getPeerId(dialog) == peerId);
    }

    public static User findUser(List<User> users, int userId) {
        return Iterables.find(users, user -> user.getId() == userId);
    }

    public static Collection<DialogItem> getPrivateDialogs(List<DialogItem> dialogs) {
        return Collections2.filter(dialogs, Predicates.and(isEmptyTitle, isPositiveUserId));
    }

    public static int[] integerListToIntArray(List<Integer> list) {
        return Ints.toArray(list);
    }

    private final static Predicate<DialogItem> isEmptyTitle = dialogItem -> TextUtils.isEmpty(dialogItem.getMessage().getTitle());

    private final static Predicate<DialogItem> isPositiveUserId = dialogItem -> dialogItem.getMessage().getUser_id() > 0;
}
