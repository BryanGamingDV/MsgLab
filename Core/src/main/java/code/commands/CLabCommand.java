package code.commands;

import code.ChatLab;
import code.PluginService;
import code.bukkitutils.sound.SoundEnum;
import code.bukkitutils.sound.SoundManager;
import code.data.UserData;
import code.managers.player.PlayerMessage;
import code.modules.DataModule;
import code.registry.ConfigManager;
import code.utils.Configuration;
import code.utils.module.ModuleCheck;
import code.utils.module.ModuleCreator;
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

    private final ChatLab plugin;

    private final PluginService pluginService;

    private final Configuration soundfile;
    private final Configuration config;
    private final Configuration messages;
    private final Configuration command;
    private final Configuration utils;

    private final ModuleCheck moduleCheck;
    private final PlayerMessage playerMethod;
    private final SoundManager sound;

    public CLabCommand(ChatLab plugin, PluginService pluginService) {
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
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {

        command.getStringList("commands.bmsg.help.pages")
                .forEach(text -> playerMethod.sendMessage(sender, text));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg help");

        return true;

    }

    @Command(names = "commands")
    public boolean commandsSubCommand(@Sender Player sender, @OptArg("1") String page) {

        if (!(getHelp().contains(page))) {
            playerMethod.sendMessage(sender, messages.getString("error.unknown-page"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        playerMethod.sendMessage(sender, command.getString("commands.bmsg.commands.format")
                .replace("%page%", page)
                .replace("%max_page%", String.valueOf(getMaxPage())));
        command.getStringList("commands.bmsg.commands.pages." + page)
                .forEach(text -> playerMethod.sendMessage(sender, text));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg commands");
        return true;
    }

    @Command(names = "reload")
    public boolean reloadSubCommand(@Sender Player sender, @OptArg("") String file) {

        if (!(playerMethod.hasPermission(sender, "commands.bmsg.reload"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (file.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("bmsg", "reload", "all, <file>")));
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
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg support");
            return true;
        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "help, reload, commands, support, sounds, debug, restore")));
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
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.sounds.disabled"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg sounds on");
            return true;
        }

        playerSound.setPlayersoundMode(true);
        playerMethod.sendMessage(sender, command.getString("commands.bmsg.sounds.enabled"));
        playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg sounds off");
        return true;
    }

    @Command(names = "debug")
    public boolean debugSubCommand(@Sender Player sender, @OptArg("") String arg1, @OptArg("") String arg2) {

        if (!(playerMethod.hasPermission(sender, "commands.bmsg.debug"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (arg1.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("bmsg", "debug", "pwc, commands, modules")));
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
                playerMethod.sendMessage(sender, command.getString("commands.bmsg.debug.list.worlds"));

                for (String worldname : worldlist) {
                    playerMethod.sendMessage(sender, "&8- &f " + worldname);
                }

                playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg debug pwc -all");
                return true;
            }

            List<String> worldname = utils.getStringList("per-world-chat.worlds." + arg2);

            if (worldname.isEmpty()) {
                playerMethod.sendMessage(sender, messages.getString("error.debug.unknown-world")
                        .replace("%world%", arg2));
                playerMethod.sendSound(sender, SoundEnum.ERROR);
                return true;
            }

            playerMethod.sendMessage(sender, command.getString("commands.bmsg.debug.worldpath-info")
                    .replace("%world%", arg2));

            for (String worldnamelist : worldname) {
                playerMethod.sendMessage(sender, "&8- &f" + worldnamelist);
            }
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg debug pwc");
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
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg debug commands");
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
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg debug modules");
            return true;

        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", arg1, "commands, modules")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "restore")
    public boolean restoreSubCommand(@Sender Player sender, @OptArg("") String type) {

        if (!(playerMethod.hasPermission(sender, "commands.bmsg.restore"))) {
            playerMethod.sendMessage(sender, messages.getString("error.no-perms"));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        if (type.isEmpty()) {
            playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                    .replace("%usage%", moduleCheck.getUsage("bmsg", "restore", "commands, modules")));
            playerMethod.sendSound(sender, SoundEnum.ERROR);
            return true;
        }

        ModuleCreator moduleCreator = pluginService.getListManager();

        if (type.equalsIgnoreCase("commands")) {
            config.set("config.modules.enabled-commands", moduleCreator.getCommands());
            config.save();
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.restore.commands"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg restore commands");
            return true;

        }
        if (type.equalsIgnoreCase("modules")) {
            config.set("config.modules.enabled-options", moduleCreator.getModules());
            config.save();
            playerMethod.sendMessage(sender, command.getString("commands.bmsg.restore.commands"));
            playerMethod.sendSound(sender, SoundEnum.ARGUMENT, "bmsg restore modules");
            return true;

        }

        playerMethod.sendMessage(sender, messages.getString("error.no-arg")
                .replace("%usage%", moduleCheck.getUsage("bmsg", "restore", "commands, modules")));
        playerMethod.sendSound(sender, SoundEnum.ERROR);
        return true;
    }

    public void getReloadEvent(Player player, String string) {

        ConfigManager files = pluginService.getFiles();

        Map<String, Configuration> fileMap = pluginService.getCache().getConfigFiles();

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
            playerMethod.sendMessage(player, files.getCommand().getString("commands.bmsg.reload"));
            playerMethod.sendSound(player, SoundEnum.ARGUMENT, "bmsg reload all");
            return;
        }

        if (fileMap.get(string) == null) {
            playerMethod.sendMessage(player, files.getMessages().getString("error.unknown-arg"));
            playerMethod.sendMessage(player, "&8- &fFiles: &a[commands, config, messages, players, sounds, utils]");
            playerMethod.sendSound(player, SoundEnum.ERROR);
            return;
        }

        fileMap.get(string).reload();
        if (string.equalsIgnoreCase("config")) {
            checkCommands();
        }
        if (string.equalsIgnoreCase("utils")){
            DataModule dataModule = new DataModule(pluginService);
        }

        playerMethod.sendMessage(player, files.getCommand().getString("commands.bmsg.reload-file")
                .replace("%file%", StringUtils.capitalize(string)));
        playerMethod.sendSound(player, SoundEnum.ARGUMENT, "bmsg reload");

    }

    public Integer getMaxPage() {
        List<String> maxpages = new ArrayList<>(getHelp());

        return maxpages.size();
    }

    public void checkCommands() {

        CommandManager commandManager = pluginService.getCommandRegistry().getCommandManager();
        commandManager.unregisterAll();

        pluginService.getCommandRegistry().reCheckCommands();
    }

    public Set<String> getHelp() {
        Configuration commands = pluginService.getFiles().getCommand();
        return commands.getConfigurationSection("commands.bmsg.commands.pages").getKeys(true);
    }
}
