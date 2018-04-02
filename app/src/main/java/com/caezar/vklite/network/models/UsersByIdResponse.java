package com.caezar.vklite.network.models;

import java.util.Arrays;

/**
 * Created by seva on 02.04.18 in 21:51.
 */

public class UsersByIdResponse {
    private Response[] response;

    public static class Response {
        private int id;
        private String first_name;
        private String last_name;
        private String photo_50;
        private String photo_100;
        private String photo_200;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
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

        public Response() {
        }

        @Override
        public String toString() {
            return "Response{" +
                    "id=" + id +
                    ", first_name='" + first_name + '\'' +
                    ", last_name='" + last_name + '\'' +
                    ", photo_50='" + photo_50 + '\'' +
                    ", photo_100='" + photo_100 + '\'' +
                    ", photo_200='" + photo_200 + '\'' +
                    '}';
        }
    }

    public Response[] getResponse() {
        return response;
    }

    public void setResponse(Response[] response) {
        this.response = response;
    }

    public UsersByIdResponse() {
    }

    @Override
    public String toString() {
        return "UsersByIdResponse{" +
                "response=" + Arrays.toString(response) +
                '}';
    }
}