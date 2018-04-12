package com.caezar.vklite.models.network;

/**
 * Created by seva on 11.04.18 in 11:54.
 */

@SuppressWarnings({"unused"})
public class PushSettings {
    private int sound;
    private int disabled_until;

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int getDisabled_until() {
        return disabled_until;
    }

    public void setDisabled_until(int disabled_until) {
        this.disabled_until = disabled_until;
    }
}
