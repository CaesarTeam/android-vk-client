package com.caezar.vklite.models.network.response;

/**
 * Created by seva on 12.04.18 in 21:28.
 */

@SuppressWarnings({"unused"})
public class UsersChatResponse {
    private Response response;

    public static class Response {
        private int[] users;

        public int[] getUsers() {
            return users;
        }

        public void setUsers(int[] users) {
            this.users = users;
        }

        public Response() {

        }
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public UsersChatResponse() {

    }
}