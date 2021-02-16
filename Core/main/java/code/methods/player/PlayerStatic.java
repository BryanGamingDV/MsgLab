package code.methods.player;

import code.MsgLab;
import code.PluginService;
import code.utils.Configuration;
import code.utils.StringFormat;
import com.google.common.xml.XmlEscapers;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerStatic {

    private static Pattern pattern;

    private static StringFormat variable;
    private static MsgLab msgLab;
    private static Configuration config;

    private PluginService pluginService;

    public PlayerStatic(PluginService pluginService) {
        this.pluginService = pluginService;
        msgLab = pluginService.getPlugin();
        variable = pluginService.getStringFormat();
        config = pluginService.getFiles().getConfig();

        String patternPath = "(?<!\\\\\\\\)(" + config.getString("options.hexcolor-format")
                .replace("%hexcolor%", "[A-Fa-f0-9]{6}") + ")";

        pattern = Pattern.compile(patternPath);
    }

    public static String setColor(String path){
        path = setHexColor(path);
        path = ChatColor.translateAlternateColorCodes('&', path);
    }


    public static String setHexColor(String path){

        if (!config.getBoolean("options.allow-hexcolor")){
            return path;
        }

        String version = Bukkit.getServer().getClass().getName();
        String versionname = version.split("\\.")[3].substring(1);

        if (!versionname.startsWith("1_16")){
            return path;
        }

        Matcher matcher = pattern.matcher(path);

        while (matcher.find()){
            msgLab.getLogger().info("Path" + path);
            String color = path.substring(matcher.start(), matcher.end());

            msgLab.getLogger().info("Color:" + color);
            msgLab.getLogger().info("Size:" + color.length());
            path = path.replace(color, "" + net.md_5.bungee.api.ChatColor.of(color));

            matcher = pattern.matcher(path);
        }

        msgLab.getLogger().info("Path now:" + path);

        return path;

    }

    public static String setColor(String path, String except){

        path = ChatColor.translateAlternateColorCodes('&', path)
                .replace("%message%", except);

        return path;
    }

    public static String setVariables(Player player, String path){
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            path =  PlaceholderAPI.setPlaceholders(player, path);
            path = path.replace('§', '&');

            return path;
        }
        return path;
    }

    public static BaseComponent[] convertText(String text){

        List<BaseComponent> baseComponentList = new ArrayList<>();
        // Format: {message, component, MODE:test}

        if (!config.getBoolean("options.allow-hover")){
            TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(text));
            baseComponentList.add(textComponent);
            return baseComponentList.toArray(new BaseComponent[0]);
        }

        String componentFormat = config.getString("options.hover-format")
                .replace("%format%", "");

        int size = 0;
        if (text.matches(".*["+ componentFormat + "].*")){

            for (String subString : text.split("[" + componentFormat + "]")) {

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
                    msgLab.getLogger().info("ERROR: The declared action value is null");
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

    public static String setPluginVariables(String path) {

        StringFormat stringFormat = msgLab.getManager().getStringFormat();
        return stringFormat.replaceString(path);
    }

    public static String setAllVariables(Player player, String path){

        path = setVariables(player, path);

        return setPluginVariables(path);
    }

    public static List<String> setPlaceholdersList(Player player, List<String> stringList) {
        stringList.replaceAll(text -> PlayerStatic.setVariables(player, text));

        return stringList;
    }

    public static List<String> setPlaceholdersList(Player player, List<String> stringList, Boolean serverreplaces) {
        if (serverreplaces){
            stringList.replaceAll(text -> PlayerStatic.setVariables(player, text)
                    .replace("%playername%", player.getName()));
        } else {
            stringList.replaceAll(text -> PlayerStatic.setVariables(player, text));
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
