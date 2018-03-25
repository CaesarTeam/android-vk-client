package com.caezar.vklite.network.modelsResponse;

/**
 * Created by seva on 25.03.18 in 19:07.
 */

/**
 * объект, описывающий личное сообщение
 */
public class Message {
    /**
     * идентификатор сообщения (не возвращается для пересланных сообщений)
     */
    private int id;

    /**
     * идентификатор пользователя, в диалоге с которым находится сообщение
     */
    private int user_id;

    /**
     * идентификатор автора сообщения
     */
    private int from_id;

    /**
     * 	дата отправки сообщения в формате Unix timestamp
     */
    private int date;

    /**
     * статус сообщения (0 — не прочитано, 1 — прочитано, не возвращается для пересланных сообщений)
     */
    private boolean read_state;

    /**
     * тип сообщения (0 — полученное, 1 — отправленное, не возвращается для пересланных сообщений)
     */
    private boolean out;

    /**
     * заголовок сообщения или беседы
     */
    private String title;

    /**
     * текст сообщения
     */
    private String body;

    /**
     * массив пересланных сообщений (если есть).
     * Максимальное количество элементов — 100.
     * Максимальная глубина вложенности для пересланных сообщений — 45.
     * Общее максимальное количество в цепочке с учетом вложенности — 500.
     */
    private Message[] fwd_messages;

    /**
     * содержатся ли в сообщении emoji-смайлы
     */
    private boolean emoji;

    /**
     * является ли сообщение важным
     */
    private boolean important;

    /**
     * удалено ли сообщение
     */
    private boolean deleted;

    /**
     * идентификатор, используемый при отправке сообщения. Возвращается только для исходящих сообщений
     */
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