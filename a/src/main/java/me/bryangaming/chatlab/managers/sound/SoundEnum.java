package me.bryangaming.chatlab.managers.sound;

public enum SoundEnum {


    ERROR("error"),
    ARGUMENT("argument"),

    RECEIVE_BROADCAST("receive_broadcast"),
    RECEIVE_BROADCASTWORLD("receive_broadcastworld"),
    RECEIVE_HELPOP("receive_helpop"),
    RECEIVE_SOCIALSPY("receive_socialspy"),
    RECEIVE_STREAM("receive_stream"),
    RECEIVE_COMMANDSPY("receive_commandspy"),

    RECEIVE_MSG("receive_msg");


    private final String typeSound;

    SoundEnum(String typeSound) {
        this.typeSound = typeSound;
    }

    public String getName() {
        return typeSound;
    }

}
