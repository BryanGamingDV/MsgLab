package atogesputo.bryangaming.chatlab.revisor.commands;

import atogesputo.bryangaming.chatlab.PluginService;
import atogesputo.bryangaming.chatlab.utils.Configuration;
import net.milkbowl.vault.economy.Economy;
import atogesputo.bryangaming.chatlab.managers.player.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConditionRevisor {


    private PluginService pluginService;

    public ConditionRevisor(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public String revisor(Player player, String command) {

        Configuration utils = pluginService.getFiles().getBasicUtils();
        Economy economy = pluginService.getSupportManager().getVaultSupport().getEconomy();

        if (!utils.getBoolean("revisor-cmd.commands-module.conditions.enabled")){
            return command;
        }

        PlayerMessage playerMethod = pluginService.getPlayerMethods().getSender();
        String commandsPath = "revisor-cmd.commands-module.conditions.commands";

        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("Vault")){
            return command;
        }

        for (String commandName : utils.getConfigurationSection(commandsPath).getKeys(false)) {

            if (!command.equalsIgnoreCase(commandName)) {
                continue;
            }

            if (utils.getString(commandsPath + "." + commandName).equalsIgnoreCase("$")){
                double playerMoney = Double.parseDouble(utils.getString(commandsPath + "." + commandName).substring(1));

                if (economy.getBalance(player) < playerMoney){
                    economy.withdrawPlayer(player, playerMoney);
                    return command;
                }

                if (utils.getBoolean("revisor-cmd.commands-module.block.op.message.enabled")) {
                    playerMethod.sendMessage(player, utils.getString("revisor-cmd.commands-module.block.op.message.format"));
                }
                return null;
            }
        }

        return command;
    }
}
