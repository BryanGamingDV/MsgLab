package me.bryangaming.chatlab.managers;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.utils.string.TextUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class ConditionManager {

    private PluginService pluginService;

    public ConditionManager(PluginService pluginService){
        this.pluginService = pluginService;
    }

    public boolean hasTheCondition(Player player, String condition){

        String firstObject = replaceVariables(player, condition.split(" ")[0]);
        String conditionType = replaceVariables(player, condition.split(" ")[1]);
        String secondObject = condition.split(" ")[2];

        switch (conditionType){
            case "<":
                return Integer.parseInt(firstObject) < Integer.parseInt(secondObject);
            case "=":
                if (TextUtils.isNumberOr(firstObject, secondObject)){
                    return Integer.parseInt(firstObject) == Integer.parseInt(secondObject);
                }
                return firstObject.equalsIgnoreCase(secondObject);

            case "{":
                return Integer.parseInt(firstObject) > Integer.parseInt(secondObject);

            default:
                return false;

        }
    }

    private String replaceVariables(Player player, String object){
        Economy economy = pluginService.getSupportManager().getVaultSupport().getEconomy();
        if (economy != null){
            object = object.replace("%pmoney%", String.valueOf(economy.getBalance(player)));
        }
        return object
                .replace("%phealth%", String.valueOf(player.getHealth()))
                .replace("%pfeed%", String.valueOf(player.getFoodLevel()))
                .replace("%pname%", player.getName());
    }

}
