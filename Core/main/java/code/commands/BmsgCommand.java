package code.commands;

import code.data.UserData;
import code.registry.ConfigManager;
import code.PluginService;
import code.methods.player.PlayerMessage;
import code.bukkitutils.SoundCreator;

import code.utils.module.ModuleCreator;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import code.MsgLab;
import code.utils.Configuration;
import code.utils.StringFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(names = {"bmsg", "bm"})
public class BmsgCommand implements CommandClass {

    private final MsgLab plugin;

    private final PluginService pluginService;

    private final Configuration soundfile;
    private final Configuration config;
    private final Configuration messages;
    private final Configuration command;
    private final Configuration utils;

    private final ModuleCheck moduleCheck;
    private final PlayerMessage playerMethod;
    private final SoundCreator sound;

    public BmsgCommand(MsgLab plugin, PluginService pluginService){
        this.plugin = plugin;
        this.pluginService = pluginService;

        this.soundfile = pluginService.getFiles().getSounds();
        this.config = pluginService.getFiles().getConfig();
        this.messages = pluginService.getFiles().getMessages();
        this.command = pluginService.getFiles().getCommand();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.moduleCheck = pluginService.getPathManager();
        this.playerMethod = pluginService.getPlayerMethods().getSender();
        this.sound = pluginService.getManagingCenter().getSoundManager();
    }

    @Command(names = "")
    public boolean onCommand(@Sender Player sender) {
        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "help, reload, commands, support, sounds, debug, restore")));
        sound.setSound(sender.getUniqueId(), "sounds.error");
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        StringFormat variable = pluginService.getStringFormat();
        variable.loopString(sender, command, "commands.bmsg.help.pages");
        return true;

    }

    @Command(names = "commands")
    public boolean commandsSubCommand(@Sender Player sender, @OptArg("1") String page) {

        if (!(getHelp().contains(page))) {
            playerMethod.sendMessage(sender, messages.getString("error.unknown-page"));
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.bmsg.commands.format")
                .replace("%page%", page)
                .replace("%max_page%", String.valueOf(getMaxPage())));
        command.getStringList("commands.bmsg.commands.pages." + page)
                .forEach(text -> playerMethod.sendMessage(sender, text));
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender, @OptArg("") String file){

        if (!(playerMethod.hasPermission(sender, "commands.bmsg.reload"))){
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;
        }

        if (file.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("bmsg", "reload", "all, <file>")));
            sound.setSound(sender.getUniqueId(), "sounds.error");
            return true;
        }

        if (file.equalsIgnoreCase("all")) {
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.load"));
            this.getReloadEvent(sender, "all");
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.bmsg.load-file"));
        this.getReloadEvent(sender, file);
        return true;
    }

    @Command(names = "support")
    public boolean supportSubCommand(@Sender Player sender){

        if (config.getBoolean("config.allow-support")) {
            playerMethod.sendMessage(sender, "&b[Server] &8| &fIf you want support of the plugin:");
            playerMethod.sendMessage(sender, "&8- &fJoin: &ahttps://discord.gg/wpSh4Bf4E");
            return true;
        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "help, reload, commands, support, sounds, debug, restore")));
        sound.setSound(sender.getUniqueId(), "sounds.error");
        return true;

    }

    @Command(names = "sounds")
    public boolean soundsSubCommand(@Sender Player sender) {
        UserData playerSound = pluginService.getCache().getPlayerUUID().get(sender.getUniqueId());

        if (!(soundfile.getBoolean("sounds.enabled-all"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-sound"));
            return true;
        }

        if (playerSound.isPlayersoundMode()) {
            playerSound.setPlayersoundMode(false);
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.sounds.disabled"));
            sound.setSound(sender.getUniqueId(), "sounds.enable-mode");
            return true;
        }

        playerSound.setPlayersoundMode(true);
        playerMethod.sendMessage(sender, command.getString("commands.bmsg.sounds.enabled"));

        return true;
    }

    @Command(names = "debug")
    public boolean debugSubCommand(@Sender Player sender, @OptArg("") String arg1, @OptArg("") String arg2) {

        if (!(playerMethod.hasPermission(sender, "commands.bmsg.debug"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;
        }

        if (arg1.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("bmsg", "debug", "pwc, commands, modules")));
            sound.setSound(sender.getUniqueId(), "sounds.error");
            return true;
        }

        if (arg1.equalsIgnoreCase("pwc")) {
            Set<String> worldlist = utils.getConfigurationSection("chat.per-world-chat.worlds").getKeys(true);

            if (arg2.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.unknown-arg"));
                playerMethod.sendMessage(sender, "&8- &fWorlds: " + String.join(", ", worldlist), "or -all");
                sound.setSound(sender.getUniqueId(), "sounds.error");
                return true;
            }

            if (arg2.equalsIgnoreCase("-all")) {
                playerMethod.sendMessage(sender, command.getString("commands.bmsg.debug.list.worlds"));
                for (String worldname : worldlist) {
                    playerMethod.sendMessage(sender, "&8- &f " + worldname);
                }
                return true;
            }

            List<String> worldname = utils.getStringList("chat.per-world-chat.worlds." + arg2);

            if (worldname.isEmpty()){
                playerMethod.sendMessage(sender, messages.getString("error.debug.unknown-world")
                        .replace("%world%", arg2));
                sound.setSound(sender.getUniqueId(), "sounds.error");
                return true;
            }

            playerMethod.sendMessage(sender, command.getString("commands.bmsg.debug.worldpath-info")
                    .replace("%world%", arg2));

            for (String worldnamelist : worldname) {
                playerMethod.sendMessage(sender, "&8- &f" + worldnamelist);
            }

            return true;
        }
        if (arg2.equalsIgnoreCase("commands")) {
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.debug.list.commands"));
            for (String commandName : pluginService.getListManager().getCommands()) {
                if (pluginService.getPathManager().isCommandEnabled(commandName)) {
                    playerMethod.sendMessage(sender, "&8- &f" + commandName + " &a[Enabled]");
                } else {
                    playerMethod.sendMessage(sender, "&8- &f" + commandName + " &c[Disabled]");
                }
            }
            return true;
        }
        if (arg2.equalsIgnoreCase("modules")) {
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.debug.list.modules"));
            for (String moduleName : pluginService.getListManager().getModules()) {
                if (pluginService.getPathManager().isOptionEnabled(moduleName)) {
                    playerMethod.sendMessage(sender, "&8- &f" + moduleName + " &a[Enabled]");
                } else {
                    playerMethod.sendMessage(sender, "&8- &f" + moduleName + " &c[Disabled]");
                }
            }
            return true;

        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", arg1, "commands, modules")));
        sound.setSound(sender.getUniqueId(), "sounds.error");
        return true;
    }

    @Command(names = "restore")
    public boolean restoreSubCommand(@Sender Player sender, @OptArg("") String type) {

        if (!(playerMethod.hasPermission(sender, "commands.bmsg.restore"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            return true;
        }

        if (type.isEmpty()){
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "bmsg", "restore", "commands, modules")));
            sound.setSound(sender.getUniqueId(), "sounds.error");
            return true;
        }

        ModuleCreator moduleCreator = pluginService.getListManager();

        if (type.equalsIgnoreCase("commands")){
            config.set("config.modules.enabled-commands", moduleCreator.getCommands());
            config.save();
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.restore.commands"));
            return true;

        }if (type.equalsIgnoreCase("modules")){
            config.set("config.modules.enabled-options", moduleCreator.getModules());
            config.save();
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.restore.commands"));
            return true;

        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "restore", "commands, modules")));
        sound.setSound(sender.getUniqueId(), "sounds.error");
        return true;
    }

    public void getReloadEvent(Player player, String string){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                PlayerMessage playersender = pluginService.getPlayerMethods().getSender();

                ConfigManager files = pluginService.getFiles();
                SoundCreator sound = pluginService.getManagingCenter().getSoundManager();

                Map<String, Configuration> fileMap = pluginService.getCache().getConfigFiles();

                if (string.equalsIgnoreCase("you")){
                    playersender.sendMessage(player, "%p &fEmmm, you are not a plugin. But symbolically, you can change your future. Be positive!");
                    playersender.sendMessage(player, "&8- &fEasterEgg #2");
                }

                if (string.equalsIgnoreCase("all")) {
                    for (Configuration config : fileMap.values()){
                        config.reload();
                    }
                    playersender.sendMessage(player, files.getCommand().getString("commands.bmsg.reload"));
                    return;
                }

                if (fileMap.get(string) == null) {
                    playersender.sendMessage(player, files.getMessages().getString("error.unknown-arg"));
                    playersender.sendMessage(player, "&8- &fFiles: &a[commands, config, messages, players, sounds, utils]");
                    sound.setSound(player.getUniqueId(), "sounds.error");
                    return;
                }

                fileMap.get(string).reload();
                playersender.sendMessage(player, files.getCommand().getString("commands.bmsg.reload-file")
                        .replace("%file%", StringUtils.capitalize(string)));
                sound.setSound(player.getUniqueId(), "sounds.on-reload");

            }
        }, 20L * 3);
    }

    public Integer getMaxPage() {
        List<String> maxpages = new ArrayList<>(getHelp());

        return maxpages.size();
    }

    public Set<String> getHelp(){
         Configuration commands = pluginService.getFiles().getCommand();
         return commands.getConfigurationSection("commands.bmsg.commands.pages").getKeys(true);
    }
}
