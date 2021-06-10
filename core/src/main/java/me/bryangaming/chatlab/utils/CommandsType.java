package me.bryangaming.chatlab.utils;

import redis.clients.jedis.commands.Commands;

public enum CommandsType {

    CLAB("clab"),
    MSG("msg-reply"),
    REPLY("msg-reply"),
    SOCIALSPY("spymodule"),
    STAFFCHAT("staff-chat"),
    HELPOP("helpop"),
    IGNORE("ignore-unignore"),
    UNIGNORE("ignore-unignore"),
    CHAT("chat"),
    BROADCAST("broadcast"),
    BROADCASTWORLD("broadcast"),
    CHANNEL("channel"),
    MOTD("motd"),
    STREAM("stream"),
    COMMANDSPY("spymodule"),
    PARTY("party"),
    ANNOUNCER("announcer");
    private String commandName;

    CommandsType(String commandName){
        this.commandName = commandName;
    }

    public String getCommandName(){
        return commandName;
    }
}
