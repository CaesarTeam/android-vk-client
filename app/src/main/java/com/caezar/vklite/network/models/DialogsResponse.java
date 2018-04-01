package com.caezar.vklite.network.models;

import android.util.Log;

import com.caezar.vklite.network.modelsResponse.Item;
import com.caezar.vklite.network.modelsResponse.Message;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.annotate.JsonValue;

import java.util.Arrays;

/**
 * Created by seva on 01.04.18 in 20:03.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DialogsResponse {
    private Response response;

    public static class Response {
        private int count;

        private int  unread_dialogs;

        private DialogItem[] items;

        public static class DialogItem extends Item {
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

            private DialogMessage message;

            public static class DialogMessage extends Message {
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

                // todo: enum
                /**
                 * тип действия (если это служебное сообщение)
                 */
                private String action;

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

                private Push_settings push_settings;


//                public enum Action {
//                    CHAT_PHOTO_UPDATE("chat_photo_update"), // обновлена фотография беседы
//                    CHAT_PHOTO_REMOVE("chat_photo_remove"), // удалена фотография беседы
//                    CHAT_CREATE("chat_create"), // создана беседа
//                    CHAT_TITLE_UPDATE("chat_title_update"), // обновлено название беседы
//                    CHAT_INVITE_USER("chat_invite_user"), // приглашен пользователь
//                    CHAT_KICK_USER("chat_kick_user"), // исключен пользователь
//                    CHAT_PIN_MESSAGE("chat_pin_message"), // закреплено сообщение
//                    CHAT_UNPIN_MESSAGE("chat_unpin_message"),// откреплено сообщение
//                    CHAT_INVITE_USER_BY_LINK("chat_invite_user_by_link"); // пользователь присоединился к беседе по ссылке
//
//                    private String action;
//
//                    Action(String action) {
//                        this.action = action;
//                    }
//
//                    public String getAction() {
//                        return action;
//                    }
//
//                    public void setAction(String action) {
//                        this.action = action;
//                    }
//                }

                public static class Push_settings {
                    private int sound;
                    private int disabled_until;

                    public int getSound() {
                        return sound;
                    }

                    public void setSound(int sound) {
                        this.sound = sound;
                    }

                    public int getDisabled_until() {
                        return disabled_until;
                    }

                    public void setDisabled_until(int disabled_until) {
                        this.disabled_until = disabled_until;
                    }
                }

                public Push_settings getPush_settings() {
                    return push_settings;
                }

                public void setPush_settings(Push_settings push_settings) {
                    this.push_settings = push_settings;
                }

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

                public String getAction() {
                    return action;
                }

                public void setAction(String action) {
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

                public DialogMessage() {
                }

                @Override
                public String toString() {
                    return "DialogMessage{" +
                            "chat_id=" + chat_id +
                            ", chat_active=" + Arrays.toString(chat_active) +
                            ", users_count=" + users_count +
                            ", admin_id=" + admin_id +
                            ", action=" + action +
                            ", action_mid=" + action_mid +
                            ", action_email='" + action_email + '\'' +
                            ", action_text='" + action_text + '\'' +
                            ", photo_50='" + photo_50 + '\'' +
                            ", photo_100='" + photo_100 + '\'' +
                            ", photo_200='" + photo_200 + '\'' +
                            ", push_settings=" + push_settings +
                            '}';
                }
            }

            public DialogItem() {
            }

            public int getUnread() {
                return unread;
            }

            public void setUnread(int unread) {
                this.unread = unread;
            }

            public int getIn_read() {
                return in_read;
            }

            public void setIn_read(int in_read) {
                this.in_read = in_read;
            }

            public int getOut_read() {
                return out_read;
            }

            public void setOut_read(int out_read) {
                this.out_read = out_read;
            }

            public DialogMessage getMessage() {
                return message;
            }

            public void setMessage(DialogMessage message) {
                this.message = message;
            }

            @Override
            public String toString() {
                return "DialogItem{" +
                        "unread=" + unread +
                        ", in_read=" + in_read +
                        ", out_read=" + out_read +
                        ", message=" + message +
                        '}';
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

        public DialogItem[] getItems() {
            return items;
        }

        public void setItems(DialogItem[] items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "count=" + count +
                    ", unread_dialogs=" + unread_dialogs +
                    ", items=" + Arrays.toString(items) +
                    '}';
        }
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public DialogsResponse() {
    }

    @Override
    public String toString() {
        return "DialogsResponse{" +
                "response=" + response +
                '}';
    }
}
