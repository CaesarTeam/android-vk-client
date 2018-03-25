package com.caezar.vklite.network.modelsResponse;

/**
 * Created by seva on 25.03.18 in 18:38.
 */

public class Dialogs {
    private int count;

    private int  unread_dialogs;

    private DialogsItems items;

    private static class DialogsItems extends Item {
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

        private DialogsMessage message;

        private static class DialogsMessage extends Message {
            /**
             * идентификатор беседы
             */
            private int chat_id;

            /**
             * идентификаторы авторов последних сообщений беседы
             */
            private int[] chat_active;

            /**
             * количество участников беседы
             */
            private int users_count;

            /**
             * идентификатор создателя беседы
             */
            private int admin_id;

            /**
             * тип действия (если это служебное сообщение)
             */
            private Action action;

            private enum Action {
                CHAT_PHOTO_UPDATE("chat_photo_update"), // обновлена фотография беседы
                CHAT_PHOTO_REMOVE("chat_photo_remove"), // удалена фотография беседы
                CHAT_CREATE("chat_create"), // создана беседа
                CHAT_TITLE_UPDATE("chat_title_update"), // обновлено название беседы
                CHAT_INVITE_USER("chat_invite_user"), // приглашен пользователь
                CHAT_KICK_USER("chat_kick_user"), // исключен пользователь
                CHAT_PIN_MESSAGE("chat_pin_message"), // закреплено сообщение
                CHAT_UNPIN_MESSAGE("chat_unpin_message"),// откреплено сообщение
                CHAT_INVITE_USER_BY_LINK("chat_invite_user_by_link"); // пользователь присоединился к беседе по ссылке
                private String action;

                Action(String action) {
                    this.action = action;
                }

                public String getAction() {
                    return action;
                }
            }

            /**
             * идентификатор пользователя (если > 0) или email (если < 0),
             * которого пригласили или исключили
             * (для служебных сообщений с action = chat_invite_user, chat_invite_user_by_link или chat_kick_user).
             * Идентификатор пользователя, который закрепил/открепил сообщение
             * для action = chat_pin_message или chat_unpin_message
             */
            private int action_mid;

            /**
             * email, который пригласили или исключили
             * (для служебных сообщений с action = chat_invite_user или chat_kick_user и отрицательным action_mid)
             */
            private String action_email;

            /**
             * название беседы (для служебных сообщений с action = chat_create или chat_title_update).
             * Текст закрепленного сообщения для action = chat_pin_message
             */
            private String action_text;

            /**
             * URL копии фотографии беседы шириной 50 px
             */
            private String photo_50;
            private String photo_100;
            private String photo_200;

            public int getChat_id() {
                return chat_id;
            }

            public void setChat_id(int chat_id) {
                this.chat_id = chat_id;
            }

            public int[] getChat_active() {
                return chat_active;
            }

            public void setChat_active(int[] chat_active) {
                this.chat_active = chat_active;
            }

            public int getUsers_count() {
                return users_count;
            }

            public void setUsers_count(int users_count) {
                this.users_count = users_count;
            }

            public int getAdmin_id() {
                return admin_id;
            }

            public void setAdmin_id(int admin_id) {
                this.admin_id = admin_id;
            }

            public Action getAction() {
                return action;
            }

            public void setAction(Action action) {
                this.action = action;
            }

            public int getAction_mid() {
                return action_mid;
            }

            public void setAction_mid(int action_mid) {
                this.action_mid = action_mid;
            }

            public String getAction_email() {
                return action_email;
            }

            public void setAction_email(String action_email) {
                this.action_email = action_email;
            }

            public String getAction_text() {
                return action_text;
            }

            public void setAction_text(String action_text) {
                this.action_text = action_text;
            }

            public String getPhoto_50() {
                return photo_50;
            }

            public void setPhoto_50(String photo_50) {
                this.photo_50 = photo_50;
            }

            public String getPhoto_100() {
                return photo_100;
            }

            public void setPhoto_100(String photo_100) {
                this.photo_100 = photo_100;
            }

            public String getPhoto_200() {
                return photo_200;
            }

            public void setPhoto_200(String photo_200) {
                this.photo_200 = photo_200;
            }

            public DialogsMessage() {
            }
        }

        public DialogsItems() {
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getUnread_dialogs() {
        return unread_dialogs;
    }

    public void setUnread_dialogs(int unread_dialogs) {
        this.unread_dialogs = unread_dialogs;
    }

    public DialogsItems getItems() {
        return items;
    }

    public void setItems(DialogsItems items) {
        this.items = items;
    }

    public Dialogs() {
    }
}