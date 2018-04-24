package com.caezar.vklite;

/**
 * Created by seva on 22.04.18 in 17:03.
 */

public class ListenerHandler<T> {
    private T listener;

    public ListenerHandler(final T listener) {
        this.listener = listener;
    }

    public void unregister() {
        listener = null;
    }
}