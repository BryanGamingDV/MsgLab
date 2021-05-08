package me.bryangaming.chatlab.revisor.message;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.Player;

public class BotRevisor implements Revisor {

    private final String revisorName;
    private final PluginService pluginService;

    public BotRevisor(PluginService pluginService, String revisorName) {
        this.pluginService = pluginService;
        this.revisorName = revisorName;
    }

    @Override
    public String getName(){
        return revisorName;
    }

    @Override
    public boolean isEnabled() {
        return pluginService.getFiles().getFormatsFile().getBoolean("filters." + revisorName + ".enabled");
    }

    public String revisor(Player player, String message) {

        Configuration formatsFile = pluginService.getFiles().getFormatsFile();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();

        if (!formatsFile.getBoolean("filters." + revisorName + ".enabled")){
            return message;
        }
        for (String keys : formatsFile.getConfigurationSection("filters." + revisorName + ".lists").getKeys(false)) {
            if (!message.startsWith(formatsFile.getString("filters." + revisorName + ".lists." + keys + ".question"))) {
                continue;
            }

            senderManager.sendMessage(player, formatsFile.getString("filters." + revisorName + ".lists." + keys + ".question"));

            if (!(formatsFile.getStringList("filters." + revisorName + ".lists." + keys + ".commands").isEmpty())) {
                formatsFile.getStringList("filters." + revisorName + ".lists." + keys + ".commands")
                        .forEach(command -> senderManager.sendCommand(player, command));
            }
        }

        return message;
    }
}
