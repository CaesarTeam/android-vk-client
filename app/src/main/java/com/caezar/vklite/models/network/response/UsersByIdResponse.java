package com.caezar.vklite.models.network.response;

import com.caezar.vklite.models.network.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by seva on 02.04.18 in 21:51.
 */

@SuppressWarnings({"unused"})
public class UsersByIdResponse {
    @JsonProperty("response")
    private User[] users;

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public UsersByIdResponse() {
    }
}