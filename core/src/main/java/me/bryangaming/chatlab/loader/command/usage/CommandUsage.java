package me.bryangaming.chatlab.loader.command.usage;


public enum CommandUsage {
    MSG("/msg <player> <message>"),
    PARTY("/party %arg% <player>"),
    IGNORE("/ignore <player>"),
    UNIGNORE("/unignore <player>");

    private final String usage;

    CommandUsage(String usage){
        this.usage = usage;
    }

    public String getUsage(){
        return usage;
    }
}
