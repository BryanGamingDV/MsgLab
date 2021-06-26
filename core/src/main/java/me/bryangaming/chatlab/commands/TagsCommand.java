package me.bryangaming.chatlab.commands;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.managers.sound.SoundEnum;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.TextUtils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


@Command(names = "tags")
public class TagsCommand implements CommandClass {

    private final PluginService pluginService;

    private final Configuration formatsFile;
    private final Configuration filtersFile;
    private final Configuration messagesFile;
    private final SenderManager senderManager;

    public TagsCommand(PluginService pluginService){
        this.pluginService = pluginService;

        this.formatsFile = pluginService.getFiles().getFormatsFile();
        this.filtersFile = pluginService.getFiles().getFiltersFile();
        this.messagesFile = pluginService.getFiles().getMessagesFile();

        this.senderManager = pluginService.getPlayerManager().getSender();
    }


    @Command(names = "")
    public boolean mainSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getString("global-errors.no-args")
                .replace("%usage%", TextUtils.getUsage("tags", "help")));
        senderManager.playSound(sender, SoundEnum.ERROR);
        return true;
    }

    @Command(names = "help")
    public boolean helpSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getStringList("tags.help"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "tags help");
        return true;
    }


    @Command(names = "use")
    public boolean useSubCommand(@Sender Player player, @OptArg("") String group, @OptArg("") String tag) {
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        if (group.isEmpty()) {
            senderManager.sendMessage(player, messagesFile.getString("tags.error.empty-group")
                    .replace("%usage%", TextUtils.getUsage("tags", "use", "[<group>]", "[<tag>]"))
                    .replace("%groups%", String.join(", ", filtersFile.getConfigurationSection("tags").getKeys(false))));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!formatsFile.contains("tags." + group)){
            senderManager.sendMessage(player, messagesFile.getString("tags.error.unknown-group")
                    .replace("%usage%", TextUtils.getUsage("tags", "use", "[<group>]", "[<tag>]"))
                    .replace("%groups%", String.join(", ", filtersFile.getConfigurationSection("tags").getKeys(false))));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        if (tag.isEmpty()) {
            senderManager.sendMessage(player, messagesFile.getString("tags.error.empty-tag")
                    .replace("%usage%", TextUtils.getUsage("tags", "use", "[<group>]", "[<tag>]"))
                    .replace("%tags%", String.join(", ", String.join("," , filtersFile.getConfigurationSection("tags." + group + ".list").getKeys(false)))));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        if (!formatsFile.contains("tags." + group + "." + tag)) {
            senderManager.sendMessage(player, messagesFile.getString("tags.error.unknown-tag")
                    .replace("%usage%", TextUtils.getUsage("tags", "use", "[<group>]", "[<tag>]"))
                    .replace("%tags%", String.join("," , filtersFile.getConfigurationSection("tags." + group + ".list").getKeys(false))));
            senderManager.playSound(player, SoundEnum.ERROR);
            return true;
        }

        userData.gethashTags().put(group, tag);
        senderManager.sendMessage(player, messagesFile.getString("tags.set")
                .replace("%group%", group)
                .replace("%tag%", tag));
        senderManager.playSound(player, SoundEnum.ERROR);
        return true;

    }

    @Command(names = "list")
    public boolean useSubCommand(@Sender Player sender) {
        senderManager.sendMessage(sender, messagesFile.getStringList("tags.list"));
        senderManager.playSound(sender, SoundEnum.ARGUMENT, "tags list");
        return true;
    }

    @Command(names = "gui")
    public boolean useSubCommand(@Sender Player sender, String group) {

        UserData userData = pluginService.getCache().getUserDatas().get(sender.getUniqueId());
        List<String> arrayList = new ArrayList<>(formatsFile.getConfigurationSection("tags").getKeys(false));

        int id;

        try {
            id = arrayList.indexOf(group);
        }catch (NullPointerException exception){
            senderManager.sendMessage(sender, messagesFile.getString("tags.error.unknown-group")
                    .replace("%usage%", TextUtils.getUsage("tags", "gui", "[<group>]"))
                    .replace("%group%", "3"));
            senderManager.playSound(sender, SoundEnum.ERROR);
            return true;
        }

        userData.setGUIGroup(0, "gui");
        userData.addGuiGroup(group);
        senderManager.openInventory(sender, "tag", id);
        return true;
    }
}
