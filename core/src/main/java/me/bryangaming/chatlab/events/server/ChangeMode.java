package me.bryangaming.chatlab.events.server;

public enum ChangeMode {
    JOIN("join"), FIRST_JOIN("first_join"), QUIT("quit");

    private final String mode;

    ChangeMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
