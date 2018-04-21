package com.caezar.vklite.models.network;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Created by seva on 03.04.18 in 16:42.
 */

@SuppressWarnings({"unused"})
public class DialogMessage extends Message {
    private int chat_id;
    private int[] chat_active;
    private int users_count;
    private int admin_id;
    private Action action;
    private int action_mid;
    private String action_email;
    private String action_text;
    private String photo_50;
    private String photo_100;
    private String photo_200;
    private PushSettings push_settings;

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Action {
        @JsonProperty("chat_photo_update")
        CHAT_PHOTO_UPDATE("chat_photo_update"), // обновлена фотография беседы
        @JsonProperty("chat_photo_remove")
        CHAT_PHOTO_REMOVE("chat_photo_remove"), // удалена фотография беседы
        @JsonProperty("chat_create")
        CHAT_CREATE("chat_create"), // создана беседа
        @JsonProperty("chat_title_update")
        CHAT_TITLE_UPDATE("chat_title_update"), // обновлено название беседы
        @JsonProperty("chat_invite_user")
        CHAT_INVITE_USER("chat_invite_user"), // приглашен пользователь
        @JsonProperty("chat_kick_user")
        CHAT_KICK_USER("chat_kick_user"), // исключен пользователь
        @JsonProperty("chat_pin_message")
        CHAT_PIN_MESSAGE("chat_pin_message"), // закреплено сообщение
        @JsonProperty("chat_unpin_message")
        CHAT_UNPIN_MESSAGE("chat_unpin_message"),// откреплено сообщение
        @JsonProperty("chat_invite_user_by_link")
        CHAT_INVITE_USER_BY_LINK("chat_invite_user_by_link"); // пользователь присоединился к беседе по ссылке

        private final String action;

        Action(String action) {
            this.action = action;
        }
    }

    public PushSettings getPush_settings() {
        return push_settings;
    }

    public void setPush_settings(PushSettings push_settings) {
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

    public DialogMessage() {
    }

    @Override
    public String toString() {
        return "chat_id=" + chat_id +
                ", chat_active=" + Arrays.toString(chat_active) +
                ", users_count=" + users_count +
                ", admin_id=" + admin_id +
                ", action=" + action +
                ", action_mid=" + action_mid +
                ", action_email=" + action_email +
                ", action_text=" + action_text +
                ", photo_50=" + photo_50 +
                ", photo_100=" + photo_100 +
                ", photo_200=" + photo_200 +
                ", push_settings=" + push_settings;
    }
}
