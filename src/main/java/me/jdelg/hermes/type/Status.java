package me.jdelg.hermes.type;

import java.util.Arrays;

public enum Status {
    ONLINE,
    OFFLINE,
    STARTING,
    STOPPING,
    UNKNOWN;

    public static Status fromString(String name) {
        return Arrays.stream(values()).filter(status -> status.name().equalsIgnoreCase(name)).findFirst().orElse(UNKNOWN);
    }
}