package me.bryangaming.chatlab.loader.command;

public enum CommandsType {

    CLAB("clab", ""),
    MSG("msg-reply", ".msg"),
    REPLY("msg-reply", ".reply"),
    SOCIALSPY("spymodule.socialspy", ""),
    STAFFCHAT("staff-chat", ""),
    HELPOP("helpop", ""),
    IGNORE("ignore-unignore", ""),
    UNIGNORE("ignore-unignore", ""),
    CHAT("chat", ""),
    BROADCAST("broadcast", ".global"),
    BROADCASTWORLD("broadcast", ".world"),
    CHANNEL("channel", ""),
    MOTD("motd", ""),
    STREAM("stream", ""),
    COMMANDSPY("spymodule.commandspy", ""),
    PARTY("party", ""),
    ANNOUNCER("announcer", "");

    private final String commandName;
    private final String suffix;

    CommandsType(String commandName, String suffix){
        this.commandName = commandName;
        this.suffix = suffix;
    }

    public String getCommandName(){
        return commandName;
    }

    public String getSuffix() {
        return suffix;
    }
}