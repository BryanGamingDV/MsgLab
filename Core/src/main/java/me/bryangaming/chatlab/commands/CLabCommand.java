package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.player.SenderManager;
import me.bryangaming.chatlab.modules.DataModule;
import me.bryangaming.chatlab.registry.FileLoader;
import me.bryangaming.chatlab.tasks.TasksManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.module.ModuleCreator;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(names = {"clab", "chatlab"})
public class CLabCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration soundfile;
    private final Configuration config;
    private final Configuration messages;
    private final Configuration command;
    private final Configuration utils;

    private final SenderManager playerMethod;

    public CLabCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.soundfile = pluginService.getFiles().getSounds();
        this.config = pluginService.getFiles().getConfig();
        this.messages = pluginService.getFiles().getMessages();
        this.command = pluginService.getFiles().getCommand();
        this.utils = pluginService.getFiles().getBasicUtils();

        this.playerMethod = pluginService.getPlayerManager().getSender();
    }

    @Command(names = "")
    public boolean onCommand(@Sender Player sender) {
        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", "help, reload, commands, support, sounds, debug, restore")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        command.getStringList("commands.clab.help.pages")
                .forEach(text -> playerMethod.sendMessage(sender, text));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab help");

        return true;

    }

    @Command(names = "commands")
    public boolean commandsSubCommand(@Sender Player sender, @OptArg("1") String page) {

        if (!(getHelp().contains(page))) {
            playerMethod.sendMessage(sender, messages.getString("error.unknown-page"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.clab.commands.format")
                .replace("%page%", page)
                .replace("%max_page%", String.valueOf(getMaxPage())));
        command.getStringList("commands.clab.commands.pages." + page)
                .forEach(text -> playerMethod.sendMessage(sender, text));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab commands");
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender, @OptArg("") String file) {

        if (!(playerMethod.hasPermission(sender, "commands.clab.reload"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (file.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", "reload", "all, <file>")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (file.equalsIgnoreCase("all")) {
            this.getReloadEvent(sender, "all");
            return true;
        }

        this.getReloadEvent(sender, file);
        return true;
    }

    @Command(names = "support")
    public boolean supportSubCommand(@Sender Player sender) {

        if (config.getBoolean("config.allow-support")) {
            playerMethod.sendMessage(sender, "&b[Server] &8| &fIf you want plugin support:");
            playerMethod.sendMessage(sender, "&8- &fJoin: &ahttps://discord.gg/wpSh4Bf4E");
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab support");
            return true;
        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", "help, reload, commands, support, sounds, debug, restore")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;

    }

    @Command(names = "sounds")
    public boolean soundsSubCommand(@Sender Player sender) {
        UserData playerSound = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(soundfile.getBoolean("sounds.enabled-all"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-sound"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (playerSound.isPlayersoundMode()) {
            playerSound.setPlayersoundMode(false);
            playerMethod.sendMessage(sender, command.getString("commands.clab.sounds.disabled"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab sounds on");
            return true;
        }

        playerSound.setPlayersoundMode(true);
        playerMethod.sendMessage(sender, command.getString("commands.clab.sounds.enabled"));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab sounds off");
        return true;
    }

    @Command(names = "debug")
    public boolean debugSubCommand(@Sender Player sender, @OptArg("") String arg1, @OptArg("") String arg2) {

        if (!(playerMethod.hasPermission(sender, "commands.clab.debug"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (arg1.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", "debug", "pwc, commands, modules")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (arg1.equalsIgnoreCase("pwc")) {
            Set<String> worldlist = utils.getConfigurationSection("per-world-chat.worlds").getKeys(true);

            if (arg2.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.unknown-arg"));
                playerMethod.sendMessage(sender, "&8- &fWorlds: " + String.join(", ", worldlist), "or -all");
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            if (arg2.equalsIgnoreCase("-all")) {
                playerMethod.sendMessage(sender, command.getString("commands.clab.debug.list.worlds"));

                for (String worldname : worldlist) {
                    playerMethod.sendMessage(sender, "&8- &f " + worldname);
                }

                playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab debug pwc -all");
                return true;
            }

            List<String> worldname = utils.getStringList("per-world-chat.worlds." + arg2);

            if (worldname.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.debug.unknown-world")
                        .replace("%world%", arg2));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            playerMethod.sendMessage(sender, command.getString("commands.clab.debug.worldpath-info")
                    .replace("%world%", arg2));

            for (String worldnamelist : worldname) {
                playerMethod.sendMessage(sender, "&8- &f" + worldnamelist);
            }
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab debug pwc");
            return true;
        }
        if (arg2.equalsIgnoreCase("commands")) {
            playerMethod.sendMessage(sender, command.getString("commands.clab.debug.list.commands"));
            for (String commandName : pluginService.getListManager().getCommands()) {
                if (pluginService.getListManager().isEnabledOption("commands", commandName)) {
                    playerMethod.sendMessage(sender, "&8- &f" + commandName + " &a[Enabled]");
                } else {
                    playerMethod.sendMessage(sender, "&8- &f" + commandName + " &c[Disabled]");
                }
            }
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab debug commands");
            return true;
        }
        if (arg2.equalsIgnoreCase("modules")) {
            playerMethod.sendMessage(sender, command.getString("commands.clab.debug.list.modules"));
            for (String moduleName : pluginService.getListManager().getModules()) {
                if (pluginService.getListManager().isEnabledOption("modules", moduleName)) {
                    playerMethod.sendMessage(sender, "&8- &f" + moduleName + " &a[Enabled]");
                } else {
                    playerMethod.sendMessage(sender, "&8- &f" + moduleName + " &c[Disabled]");
                }
            }
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab debug modules");
            return true;

        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", arg1, "commands, modules")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "restore")
    public boolean restoreSubCommand(@Sender Player sender, @OptArg("") String type) {

        if (!(playerMethod.hasPermission(sender, "commands.clab.restore"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (type.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", "restore", "commands, modules")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        ModuleCreator moduleCreator = pluginService.getListManager();

        if (type.equalsIgnoreCase("commands")) {
            config.set("config.modules.enabled-commands", moduleCreator.getCommands());
            config.save();
            playerMethod.sendMessage(sender, command.getString("commands.clab.restore.commands"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab restore commands");
            return true;

        }
        if (type.equalsIgnoreCase("modules")) {
            config.set("config.modules.enabled-options", moduleCreator.getModules());
            config.save();
            playerMethod.sendMessage(sender, command.getString("commands.clab.restore.commands"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "clab restore modules");
            return true;

        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", "restore", "commands, modules")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    public void getReloadEvent(Player player, String string) {

        FileLoader files = pluginService.getFiles();

        Map<String, Configuration> fileMap = pluginService.getCache().getConfigFiles();
        TasksManager tasksManager = pluginService.getTasksManager();

        if (string.equalsIgnoreCase("you")) {
            playerMethod.sendMessage(player, "%p &fEmmm, you are not a plugin. But symbolically, you can change your future. Be positive!");
            playerMethod.sendMessage(player, "&8- &fEasterEgg #2");
        }

        if (string.equalsIgnoreCase("all")) {
            for (Configuration config : fileMap.values()) {
                config.reload();
            }
            checkCommands();
            DataModule dataModule = new DataModule(pluginService);
            tasksManager.reloadTasks();
            playerMethod.sendMessage(player, files.getCommand().getString("commands.clab.reload"));
            playerMethod.sendSound(player, SoundEnum.ARGUMENT, "clab reload all");
            return;
        }

        if (fileMap.get(string) == null) {
            playerMethod.sendMessage(player, files.getMessages().getString("error.unknown-arg"));
            playerMethod.sendMessage(player, "&8- &fFiles: &a[commands, config, messages, players, sounds, utils]");
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        fileMap.get(string).reload();
        if (string.equalsIgnoreCase("command")) {
            tasksManager.getTask("announcer").reloadTask();
        }
        if (string.equalsIgnoreCase("config")) {
            checkCommands();
        }
        if (string.equalsIgnoreCase("utils")) {
            DataModule dataModule = new DataModule(pluginService);
        }

        playerMethod.sendMessage(player, files.getCommand().getString("commands.clab.reload-file")
                .replace("%file%", StringUtils.capitalize(string)));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "clab reload");

    }

    public Integer getMaxPage() {
        List<String> maxpages = new ArrayList<>(getHelp());

        return maxpages.size();
    }

    public void checkCommands() {

        CommandManager commandManager = pluginService.getCommandLoader().getCommandManager();
        commandManager.unregisterAll();

        pluginService.getCommandLoader().reCheckCommands();
    }

    public Set<String> getHelp() {
        Configuration commands = pluginService.getFiles().getCommand();
        return commands.getConfigurationSection("commands.clab.commands.pages").getKeys(true);
    }
}
