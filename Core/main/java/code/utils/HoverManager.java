package code.utils;

import code.methods.player.PlayerStatic;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;


public class HoverManager{

    private final TextComponent textComponent;

    public HoverManager(String string) {
        textComponent = new TextComponent(string);
    }

    public TextComponent getTextComponent(){
        return textComponent;
    }

    public void setHover(String hovertext, String clickcommand){
        hovertext = PlayerStatic.setColor(hovertext);

        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hovertext).create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickcommand));
    }

    public void setHover(String hovertext, String clickcommand, Boolean forcecommand){
        hovertext = PlayerStatic.setColor(hovertext);

        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hovertext).create()));
        if (forcecommand) {
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickcommand));
        }
    }


    public String hoverMessage(Player player, List<String> string){
        String path = String.join("\n", string);
        String pathcolored = PlayerStatic.setColor(path)
                .replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));


        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlayerStatic.setVariables(player, pathcolored);
        }else{
            return pathcolored;
        }
    }

}
