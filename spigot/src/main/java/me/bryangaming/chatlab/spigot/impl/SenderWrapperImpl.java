package me.bryangaming.chatlab.spigot.impl;

import me.bryangaming.chatlab.common.wrapper.SenderWrapper;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SenderWrapperImpl
        implements SenderWrapper {


    private final CommandSender commandSender;

    public SenderWrapperImpl(CommandSender commandSender){
        this.commandSender = commandSender;
    }

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public void sendMessage(List<String> messages){
        messages.forEach(commandSender::sendMessage);
    }


    @Override
    public String getName(){
        return commandSender.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

}
