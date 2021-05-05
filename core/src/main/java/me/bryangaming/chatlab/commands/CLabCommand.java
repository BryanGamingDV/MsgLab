package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.loader.FileLoader;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.modules.DataModule;
import me.bryangaming.chatlab.tasks.TasksManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.module.ModuleType;
import me.bryangaming.chatlab.utils.module.ModuleUtils;
import me.bryangaming.chatlab.utils.string.TextUtils;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(names = {"clab", "chatlab"})
public class CLabCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration configFile;
    private final Configuration soundsFile;
    private final Configuration messagesFile;
    private final Configuration commandFile;
    private final Configuration formatsFile;

    private final SenderManager senderManager;

    public CLabCommand(PluginService pluginService) {
        this.pluginService = pluginService;

        this.configFile = pluginService.getFiles().getConfigFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();
        this.commandFile = pluginService.getFiles().getCommandFile();
        this.soundsFile = pluginService.getFiles().getSoundsFile();
        this.formatsFile = pluginService.getFiles().getFormatsFile();

        this.senderManager = pluginService.getPlayerManager().getSender();
    }

    @Command(names = "")
    public boolean onCommand(CommandSender sender) {
        senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", "help, reload, commands, support, sounds, debug, restore")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(CommandSender sender) {

        commandFile.getStringList("commands.clab.help.pages")
                .forEach(text -> senderManager.sendMessage(sender, text));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab help");

        return true;

    }

    @Command(names = "commands")
    public boolean commandsSubCommand(CommandSender sender, @OptArg("1") String page) {

        if (!(getHelp().contains(page))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.unknown-page"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.clab.commands.format")
                .replace("%page%", page)
                .replace("%max_page%", String.valueOf(getMaxPage())));
        commandFile.getStringList("commands.clab.commands.pages." + page)
                .forEach(text -> senderManager.sendMessage(sender, text));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab commands");
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(CommandSender sender, @OptArg("") String file) {
        System.out.println(sender.getName());
        if (!(senderManager.hasPermission(sender, "commands.clab.reload"))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (file.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", "reload", "all, <file>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
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
    public boolean supportSubCommand(CommandSender sender) {

        if (configFile.getBoolean("options.allow-support")) {
            senderManager.sendMessage(sender, "&b[Server] &8| &fIf you want plugin support:");
            senderManager.sendMessage(sender, "&8- &fJoin: &ahttps://discord.gg/wpSh4Bf4E");
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab support");
            return true;
        }

        senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", "help, reload, commands, sounds, debug, restore")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;

    }

    @Command(names = "sounds")
    public boolean soundsSubCommand(@Sender Player sender) {
        UserData playerSound = pluginService.getCache().getUserDatas().get(sender.getUniqueId());

        if (!(soundsFile.getBoolean("sounds.enabled-all"))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-sound"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (playerSound.isPlayersoundMode()) {
            playerSound.setPlayersoundMode(false);
            senderManager.sendMessage(sender, commandFile.getString("commands.clab.sounds.disabled"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab sounds on");
            return true;
        }

        playerSound.setPlayersoundMode(true);
        senderManager.sendMessage(sender, commandFile.getString("commands.clab.sounds.enabled"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab sounds off");
        return true;
    }

    @Command(names = "debug")
    public boolean debugSubCommand(CommandSender sender, @OptArg("") String arg1, @OptArg("") String arg2) {

        if (!(senderManager.hasPermission(sender, "commands.clab.debug"))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (arg1.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", "debug", "pwc, commands, modules")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (arg1.equalsIgnoreCase("pwc")) {
            Set<String> worldlist = formatsFile.getConfigurationSection("per-world-chat.worlds").getKeys(true);

            if (arg2.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("error.unknown-arg"));
                senderManager.sendMessage(sender, "&8- &fWorlds: " + String.join(", ", worldlist) + ", or -all");
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            if (arg2.equalsIgnoreCase("-all")) {
                senderManager.sendMessage(sender, commandFile.getString("commands.clab.debug.list.worlds"));

                for (String worldname : worldlist) {
                    senderManager.sendMessage(sender, "&8- &f " + worldname);
                }

                senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab debug pwc -all");
                return true;
            }

            List<String> worldname = formatsFile.getStringList("per-world-chat.worlds." + arg2);

            if (worldname.isEmpty()) {
                senderManager.sendMessage(sender, messagesFile.getString("error.debug.unknown-world")
                        .replace("%world%", arg2));
                senderManager.playSound(sender, SoundEnum.ERROR);
                return true;
            }

            senderManager.sendMessage(sender, commandFile.getString("commands.clab.debug.worldpath-info")
                    .replace("%world%", arg2));

            for (String worldnamelist : worldname) {
                senderManager.sendMessage(sender, "&8- &f" + worldnamelist);
            }
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab debug pwc");
            return true;
        }
        if (arg2.equalsIgnoreCase("commands")) {
            senderManager.sendMessage(sender, commandFile.getString("commands.clab.debug.list.commands"));
            for (String commandName : pluginService.getListManager().getCommands()) {
                if (pluginService.getListManager().isEnabledOption(ModuleType.COMMAND, commandName)) {
                    senderManager.sendMessage(sender, "&8- &f" + commandName + " &a[Enabled]");
                } else {
                    senderManager.sendMessage(sender, "&8- &f" + commandName + " &c[Disabled]");
                }
            }
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab debug commands");
            return true;
        }
        if (arg2.equalsIgnoreCase("modules")) {
            senderManager.sendMessage(sender, commandFile.getString("commands.clab.debug.list.modules"));
            for (String moduleName : pluginService.getListManager().getModules()) {
                if (pluginService.getListManager().isEnabledOption(ModuleType.MODULE, moduleName)) {
                    senderManager.sendMessage(sender, "&8- &f" + moduleName + " &a[Enabled]");
                } else {
                    senderManager.sendMessage(sender, "&8- &f" + moduleName + " &c[Disabled]");
                }
            }
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab debug modules");
            return true;

        }

        senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", arg1, "commands, modules")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }
    @Command(names = "config")
    public boolean onConfig(CommandSender sender, @OptArg("") String configName, @OptArg("") String path, @OptArg("") String value){

        if (configName.isEmpty()){
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", "<config>", "<path>", "<value>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        Configuration config = pluginService.getCache().getConfigFiles().get(configName);

        if (config == null){
            senderManager.sendMessage(sender, messagesFile.getString("error.clab.config.unknown-config")
                    .replace("%listconfig%", String.join(" ,", pluginService.getCache().getConfigFiles().keySet())));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (path.isEmpty()){
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", configName, "<path>", "<value>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (config.get(path) == null){
            senderManager.sendMessage(sender, messagesFile.getString("error.unknown-path"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (value.isEmpty()){
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", configName, "<path>", "<value>")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        senderManager.sendMessage(sender, commandFile.getString("commands.clab.config.set")
                .replace("%config%", configName + ".yml")
                .replace("%path%", path)
                .replace("%value%", value));
        senderManager.playSound(sender, SoundEnum.ERROR);
        config.set(path, value);
        config.save();
        return true;
    }
    @Command(names = "restore")
    public boolean onRestoreSubCommand(CommandSender sender, @OptArg("") String type) {

        if (!(senderManager.hasPermission(sender, "commands.clab.restore"))) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-perms"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (type.isEmpty()) {
            senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                    .replace("%usage%", TextUtils.getUsage("clab", "restore", "commands, modules")));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        ModuleUtils moduleUtils = pluginService.getListManager();

        if (type.equalsIgnoreCase("commands")) {
            configFile.set("config.modules.enabled-commands", moduleUtils.getCommands());
            configFile.save();
            senderManager.sendMessage(sender, commandFile.getString("commands.clab.restore.commands"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab restore commands");
            return true;

        }
        if (type.equalsIgnoreCase("modules")) {
            configFile.set("config.modules.enabled-options", moduleUtils.getModules());
            configFile.save();
            senderManager.sendMessage(sender, commandFile.getString("commands.clab.restore.commands"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab restore modules");
            return true;

        }

        senderManager.sendMessage(sender, messagesFile.getString("error.no-arg")
                .replace("%usage%", TextUtils.getUsage("clab", "restore", "commands, modules")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    public void getReloadEvent(CommandSender sender, String string) {

        FileLoader files = pluginService.getFiles();

        Map<String, Configuration> fileMap = pluginService.getCache().getConfigFiles();
        TasksManager tasksManager = pluginService.getTasksManager();

        if (string.equalsIgnoreCase("you")) {
            senderManager.sendMessage(sender, "%p &fEmmm, you are not a plugin. But symbolically, you can change your future. Be positive!");
            senderManager.sendMessage(sender, "&8- &fEasterEgg #2");
        }

        if (string.equalsIgnoreCase("all")) {
            for (Configuration config : fileMap.values()) {
                config.reload();
            }
            checkCommands();
            DataModule dataModule = new DataModule(pluginService);
            tasksManager.reloadTasks();
            senderManager.sendMessage(sender, files.getCommandFile().getString("commands.clab.reload"));
            senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab reload all");
            return;
        }

        if (fileMap.get(string) == null) {
            senderManager.sendMessage(sender, files.getMessagesFile().getString("error.unknown-arg"));
            senderManager.sendMessage(sender, "&8- &fFiles: &a[commands, config, messages, players, sounds, utils]");
            senderManager.playSound(sender, SoundEnum.ERROR);
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

        senderManager.sendMessage(sender, files.getCommandFile().getString("commands.clab.reload-file")
                .replace("%file%", StringUtils.capitalize(string)));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "clab reload");

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
        Configuration commands = pluginService.getFiles().getCommandFile();
        return commands.getConfigurationSection("commands.clab.commands.pages").getKeys(true);
    }
}
