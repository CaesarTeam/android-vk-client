package com.caezar.vklite.models.response;

/**
 * Created by seva on 07.04.18 in 0:06.
 */

@SuppressWarnings({"unused"})
public class ErrorVkApiResponse {
    private Error error;

    public static class Error {
        private int error_code;
        private String error_msg;

        public Error() {
        }

        public int getError_code() {
            return error_code;
        }

        public void setError_code(int error_code) {
            this.error_code = error_code;
        }

        public String getError_msg() {
            return error_msg;
        }

        public void setError_msg(String error_msg) {
            this.error_msg = error_msg;
        }
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public ErrorVkApiResponse() {
    }
}
