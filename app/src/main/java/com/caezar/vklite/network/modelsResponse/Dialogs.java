package com.caezar.vklite.network.modelsResponse;

/**
 * Created by seva on 25.03.18 in 18:38.
 */

public class Dialogs {
    private int count;

    private int  unread_dialogs;

    private DialogsItems items;

    private static class DialogsItems {
        /**
         * количество непрочитанных сообщений (если все сообщения прочитаны, поле не возвращается)
         */
        private int  unread;

        /**
         * идентификатор последнего сообщения, прочитанного текущим пользователем
         */
        private int  in_read;

        /**
         * идентификатор последнего сообщения, прочитанного собеседником
         */
        private int  out_read;

        private Message message;
    }
}