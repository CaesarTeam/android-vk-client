package com.caezar.vklite.models;

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
    private Attachments[] attachments;

    public Attachments[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachments[] attachments) {
        this.attachments = attachments;
    }

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

    public static class Attachments {
        // todo: enum
        private String type;
        private Photo photo;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Photo getPhoto() {
            return photo;
        }

        public void setPhoto(Photo photo) {
            this.photo = photo;
        }

        public Attachments() {

        }

        public static class Photo {
            private String photo_75;
            private String photo_130;
            private String photo_604;
            private String photo_807;
            private String photo_1280;
            private String photo_2560;

            public String getPhoto_75() {
                return photo_75;
            }

            public void setPhoto_75(String photo_75) {
                this.photo_75 = photo_75;
            }

            public String getPhoto_130() {
                return photo_130;
            }

            public void setPhoto_130(String photo_130) {
                this.photo_130 = photo_130;
            }

            public String getPhoto_604() {
                return photo_604;
            }

            public void setPhoto_604(String photo_604) {
                this.photo_604 = photo_604;
            }

            public String getPhoto_807() {
                return photo_807;
            }

            public void setPhoto_807(String photo_807) {
                this.photo_807 = photo_807;
            }

            public String getPhoto_1280() {
                return photo_1280;
            }

            public void setPhoto_1280(String photo_1280) {
                this.photo_1280 = photo_1280;
            }

            public String getPhoto_2560() {
                return photo_2560;
            }

            public void setPhoto_2560(String photo_2560) {
                this.photo_2560 = photo_2560;
            }

            public Photo() {
            }
        }
    }

    public Message() {

    }
}