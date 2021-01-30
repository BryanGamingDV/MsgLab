package code.commands;

import code.cache.UserData;
import code.registry.ConfigManager;
import code.Manager;
import code.methods.player.PlayerMessage;
import code.bukkitutils.SoundManager;

import code.utils.module.ModuleCreator;
import code.utils.module.ModuleCheck;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import code.BasicMsg;
import code.utils.Configuration;
import code.utils.StringFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(names = {"bmsg", "bm"})
public class BmsgCommand implements CommandClass {

    private final BasicMsg plugin;

    private final Manager manager;

    private final Configuration soundfile;
    private final Configuration config;
    private final Configuration messages;
    private final Configuration command;
    private final Configuration utils;

    private final ModuleCheck moduleCheck;
    private final PlayerMessage playersender;
    private final SoundManager sound;

    public BmsgCommand(BasicMsg plugin, Manager manager){
        this.plugin = plugin;
        this.manager = manager;

        this.soundfile = manager.getFiles().getSounds();
        this.config = manager.getFiles().getConfig();
        this.messages = manager.getFiles().getMessages();
        this.command = manager.getFiles().getCommand();
        this.utils = manager.getFiles().getBasicUtils();

        this.moduleCheck = manager.getPathManager();
        this.playersender = manager.getPlayerMethods().getSender();
        this.sound = manager.getManagingCenter().getSoundManager();
    }

    @Command(names = "")
    public boolean onCommand(@Sender Player player) {
        playersender.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "help, reload, commands, support, sounds, debug, restore")));
        sound.setSound(player.getUniqueId(), "sounds.error");
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player player) {

        StringFormat variable = manager.getStringFormat();
        variable.loopString(player, command, "commands.bmsg.help.pages");
        return true;

    }

    @Command(names = "commands")
    public boolean commandsSubCommand(@Sender Player player, @OptArg("1") String page) {

        if (!(getHelp().contains(page))) {
            playersender.sendMessage(player, messages.getString("error.unknown-page"));
            return true;
        }

        playersender.sendMessage(player, command.getString("commands.bmsg.commands.format")
                .replace("%page%", page)
                .replace("%max_page%", String.valueOf(getMaxPage())));
        command.getStringList("commands.bmsg.commands.pages." + page)
                .forEach(text -> playersender.sendMessage(player, text));
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player player, @OptArg("") String file){

            if (!(player.hasPermission(config.getString("config.perms.reload")))) {
                playersender.sendMessage(player, messages.getString("error.no-perms"));
                return true;
            }

            if (file.isEmpty()) {
                playersender.sendMessage(player, messages.getString("error.no-arg")
                        .replace("%usage%", moduleCheck.getUsage("bmsg", "reload", "all, <file>")));
                sound.setSound(player.getUniqueId(), "sounds.error");
                return true;
            }

            if (file.equalsIgnoreCase("all")) {
                playersender.sendMessage(player, command.getString("commands.bmsg.load"));
                this.getReloadEvent(player, "all");
                return true;

            }

            playersender.sendMessage(player, command.getString("commands.bmsg.load-file"));
            this.getReloadEvent(player, file);
            return true;
    }

    @Command(names = "support")
    public boolean supportSubCommand(@Sender Player player){

        if (config.getBoolean("config.allow-support")) {
            playersender.sendMessage(player, "&b[Server] &8| &fIf you want support of the plugin:");
            playersender.sendMessage(player, "&8- &fJoin: &ahttps://discord.gg/wpSh4Bf4E");
            return true;
        }

        playersender.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "help, reload, commands, support, sounds, debug, restore")));
        sound.setSound(player.getUniqueId(), "sounds.error");
        return true;

    }

    @Command(names = "sounds")
    public boolean soundsSubCommand(@Sender Player player) {
        UserData playerSound = manager.getCache().getPlayerUUID().get(player.getUniqueId());

        if (!(soundfile.getBoolean("sounds.enabled-all"))) {
            playersender.sendMessage(player, messages.getString("error.no-sound"));
            return true;
        }

        if (playerSound.isPlayersoundMode()) {
            playerSound.setPlayersoundMode(false);
            playersender.sendMessage(player, command.getString("commands.bmsg.sounds.disabled"));
            sound.setSound(player.getUniqueId(), "sounds.enable-mode");
            return true;
        }

        playerSound.setPlayersoundMode(true);
        playersender.sendMessage(player, command.getString("commands.bmsg.sounds.enabled"));

        return true;
    }

    @Command(names = "debug")
    public boolean debugSubCommand(@Sender Player player, @OptArg("") String arg1, @OptArg("") String arg2) {

         if (!(player.hasPermission(config.getString("config.perms.debug")))) {
            playersender.sendMessage(player, messages.getString("error.no-perms"));
            return true;
        }

        if (arg1.isEmpty()) {
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("bmsg", "debug", "pwc, commands, modules")));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        if (arg1.equalsIgnoreCase("pwc")) {
            Set<String> worldlist = utils.getConfigurationSection("utils.chat.per-world-chat.worlds").getKeys(true);

            if (arg2.isEmpty()) {
                playersender.sendMessage(player, messages.getString("error.unknown-arg"));
                playersender.sendMessage(player, "&8- &fWorlds: " + String.join(", ", worldlist), "or -all");
                sound.setSound(player.getUniqueId(), "sounds.error");
                return true;
            }

            if (arg2.equalsIgnoreCase("-all")) {
                playersender.sendMessage(player, command.getString("commands.bmsg.debug.list.worlds"));
                for (String worldname : worldlist) {
                    playersender.sendMessage(player, "&8- &f " + worldname);
                }
                return true;
            }

            List<String> worldname = utils.getStringList("utils.chat.per-world-chat.worlds." + arg2);

            if (worldname.isEmpty()){
                playersender.sendMessage(player, messages.getString("error.debug.unknown-world")
                        .replace("%world%", arg2));
                sound.setSound(player.getUniqueId(), "sounds.error");
                return true;
            }

            playersender.sendMessage(player, command.getString("commands.bmsg.debug.worldpath-info")
                    .replace("%world%", arg2));

            for (String worldnamelist : worldname) {
                playersender.sendMessage(player, "&8- &f" + worldnamelist);
            }

            return true;
        }
        if (arg2.equalsIgnoreCase("commands")) {
            playersender.sendMessage(player, command.getString("commands.bmsg.debug.list.commands"));
            for (String commandName : manager.getListManager().getCommands()) {
                if (manager.getPathManager().isCommandEnabled(commandName)) {
                    playersender.sendMessage(player, "&8- &f" + commandName + " &a[Enabled]");
                } else {
                    playersender.sendMessage(player, "&8- &f" + commandName + " &c[Disabled]");
                }
            }
            return true;
        }
        if (arg2.equalsIgnoreCase("modules")) {
            playersender.sendMessage(player, command.getString("commands.bmsg.debug.list.modules"));
            for (String moduleName : manager.getListManager().getModules()) {
                if (manager.getPathManager().isOptionEnabled(moduleName)) {
                    playersender.sendMessage(player, "&8- &f" + moduleName + " &a[Enabled]");
                } else {
                    playersender.sendMessage(player, "&8- &f" + moduleName + " &c[Disabled]");
                }
            }
            return true;

        }

        playersender.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", arg1, "commands, modules")));
        sound.setSound(player.getUniqueId(), "sounds.error");
        return true;
    }

    @Command(names = "restore")
    public boolean restoreSubCommand(@Sender Player player, @OptArg("") String type) {
        if (type.isEmpty()){
            playersender.sendMessage(player, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage( "bmsg", "restore", "commands, modules")));
            sound.setSound(player.getUniqueId(), "sounds.error");
            return true;
        }

        ModuleCreator moduleCreator = manager.getListManager();

        if (type.equalsIgnoreCase("commands")){
            config.set("config.modules.enabled-commands", moduleCreator.getCommands());
            config.save();
            playersender.sendMessage(player, command.getString("commands.bmsg.restore.commands"));
            return true;

        }if (type.equalsIgnoreCase("modules")){
            config.set("config.modules.enabled-options", moduleCreator.getModules());
            config.save();
            playersender.sendMessage(player, command.getString("commands.bmsg.restore.commands"));
            return true;

        }

        playersender.sendMessage(player, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "restore", "commands, modules")));
        sound.setSound(player.getUniqueId(), "sounds.error");
        return true;
    }

    public void getReloadEvent(Player player, String string){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                PlayerMessage playersender = manager.getPlayerMethods().getSender();

                ConfigManager files = manager.getFiles();
                SoundManager sound = manager.getManagingCenter().getSoundManager();

                Map<String, Configuration> fileMap = manager.getCache().getConfigFiles();

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
         Configuration commands = manager.getFiles().getCommand();
         return commands.getConfigurationSection("commands.bmsg.commands.pages").getKeys(true);
    }
}
