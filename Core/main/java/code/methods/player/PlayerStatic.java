package code.methods.player;

import code.BasicMsg;
import code.PluginService;
import code.utils.StringFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatic {

    private static StringFormat variable;
    private static BasicMsg basicMsg;
    private PluginService pluginService;

    public PlayerStatic(PluginService pluginService) {
        this.pluginService = pluginService;
        basicMsg = pluginService.getPlugin();
        variable = pluginService.getStringFormat();
    }

    public static String setColor(String path){
        return ChatColor.translateAlternateColorCodes('&', path);
    }

    public static String setColor(String path, String except){
        return ChatColor.translateAlternateColorCodes('&', path)
                .replace("%message%", except);
    }

    public static String setVariables(Player player, String path){
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, path);
        }
        return path;
    }
    public static BaseComponent[] convertText(String text){

        List<BaseComponent> baseComponentList = new ArrayList<>();
        // Format: {message, component, MODE:test}

        int size = 0;
        if (text.contains("{") && text.contains("}")){

            for (String subString : text.split("[{}]")) {

                if (subString.isEmpty()){
                    continue;
                }

                if (!subString.contains(",")){

                    TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(subString));
                    textComponent.setText(subString);
                    baseComponentList.add(textComponent);
                    size++;
                    continue;
                }

                String[] componentText = subString.split(",");

                if (componentText.length < 3){

                    TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(componentText[0]));

                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(componentText[1]).create()));
                    baseComponentList.add(textComponent);
                    size++;
                    continue;
                }

                String[] componentCommand = componentText[2].split(":");

                try {
                    ClickEvent.Action.valueOf(componentCommand[0].toUpperCase());
                }catch (IllegalArgumentException illegalArgumentException){
                    basicMsg.getLogger().info("ERROR: The action value that you put is null");
                    return baseComponentList.toArray(new BaseComponent[size]);
                }

                TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(componentText[0]));

                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(componentText[1]).create()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(componentCommand[0].toUpperCase()), componentCommand[1]));
                baseComponentList.add(textComponent);

                size++;
            }

        }else{
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(text));
            baseComponentList.add(textComponent);
        }

        return baseComponentList.toArray(new BaseComponent[size]);
    }

    public static String setFormat(Player player, String path){
        path = setVariables(player, path);
        path = variable.replaceString(path);

        return PlayerStatic.setColor(path);
    }
    public static List<String> setPlaceholdersList(Player player, List<String> stringList) {
        stringList.replaceAll(text -> PlayerStatic.setFormat(player, text));

        return stringList;
    }

    public static List<String> setPlaceholdersList(Player player, List<String> stringList, Boolean serverreplaces) {
        if (serverreplaces){
            stringList.replaceAll(text -> PlayerStatic.setFormat(player, text)
                    .replace("%playername%", player.getName()));
        } else {
            stringList.replaceAll(text -> PlayerStatic.setFormat(player, text));
        }
        return stringList;
    }

    public static List<String> setColorList( List<String> stringList){

        List<String> newColor = new ArrayList<>();

        for (String string : stringList){
            newColor.add(setColor(string));
        }

        return newColor;
    }
}
