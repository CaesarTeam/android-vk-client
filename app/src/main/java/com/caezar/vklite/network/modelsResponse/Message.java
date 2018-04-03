package com.caezar.vklite.network.modelsResponse;

/**
 * Created by seva on 25.03.18 in 19:07.
 */

public class Message {
    private int id;
    private int user_id;
    private int from_id;
    private int date;
    private boolean read_state;
    private boolean out;
    private String title;
    private String body;
    private Message[] fwd_messages;
    private boolean emoji;
    private boolean important;
    private boolean deleted;
    private int random_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public boolean isRead_state() {
        return read_state;
    }

    public void setRead_state(boolean read_state) {
        this.read_state = read_state;
    }

    public boolean isOut() {
        return out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Message[] getFwd_messages() {
        return fwd_messages;
    }

    public void setFwd_messages(Message[] fwd_messages) {
        this.fwd_messages = fwd_messages;
    }

    public boolean isEmoji() {
        return emoji;
    }

    public void setEmoji(boolean emoji) {
        this.emoji = emoji;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getRandom_id() {
        return random_id;
    }

    public void setRandom_id(int random_id) {
        this.random_id = random_id;
    }

    public Message() {

    }
}