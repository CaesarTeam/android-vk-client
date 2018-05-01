package com.caezar.vklite.libs;

import com.caezar.vklite.models.Message;
import com.google.common.collect.Iterables;

import java.util.List;

/**
 * Created by seva on 01.05.18 in 14:05.
 */

public class Guava {
    public static int findIndexMessage(List<Message> messages, Message newMessage) {
        return Iterables.indexOf(messages, message -> message.getId() == newMessage.getId());
    }
}
