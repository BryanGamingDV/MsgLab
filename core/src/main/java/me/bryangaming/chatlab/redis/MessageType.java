package me.bryangaming.chatlab.redis;

public enum MessageType {
    SENDMESSAGE("sendMessage"),
    MSG("msg"),
    STAFFCHAT("staffchat"),
    REPLY("reply"),
    STREAM("stream"),
    HELPOP("helpop"),
    BROADCAST("broadcast");

    private final String messageType;

    MessageType(String messageType){
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
