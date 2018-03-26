package com.caezar.vklite.network.modelsRequest;

/**
 * Created by seva on 25.03.18 in 18:15.
 */
// todo: annotation unsigned, max, min

public class Dialogs {
    /**
     * смещение, необходимое для выборки определенного подмножества диалогов
     * целое
     */
    private int offset;

    /**
     * количество диалогов, которое необходимо получить
     * максимум 200
     * положительное
     */
    private int count = 20;

    /**
     * идентификатор сообщения, начиная с которого нужно вернуть список диалогов
     * положительное
     */
    private long start_message_id;

    /**
     * Количество символов, по которому нужно обрезать сообщение. Укажите 0, если Вы не хотите обрезать сообщение
     * Обратите внимание, текст обрезается по словам, точное число символов может не совпадать с указанным значением
     * положительное
     */
    private int preview_length;

    /**
     *  true — вернуть только диалоги, в которых есть непрочитанные входящие сообщения
     */
    private boolean unread;

    /**
     *  true — вернуть диалоги с пометкой «Важные» (для сообщений сообществ).
     */
    private boolean important;

    /**
     *  true — вернуть диалоги с пометкой «Неотвеченные» (для сообщений сообществ).
     */
    private boolean unanswered;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getStart_message_id() {
        return start_message_id;
    }

    public void setStart_message_id(long start_message_id) {
        this.start_message_id = start_message_id;
    }

    public int getPreview_length() {
        return preview_length;
    }

    public void setPreview_length(int preview_length) {
        this.preview_length = preview_length;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isUnanswered() {
        return unanswered;
    }

    public void setUnanswered(boolean unanswered) {
        this.unanswered = unanswered;
    }

    public Dialogs() {

    }

    @Override
    public String toString() {
        return  "offset=" + offset +
                "& count=" + count +
                "& start_message_id=" + start_message_id +
                "& preview_length=" + preview_length +
                "& unread=" + unread +
                "& important=" + important +
                "& unanswered=" + unanswered;
    }
}