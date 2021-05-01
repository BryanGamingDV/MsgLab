package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.revisor.commands.BlockCmdRevisor;
import me.bryangaming.chatlab.revisor.commands.ConditionRevisor;
import me.bryangaming.chatlab.revisor.message.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RevisorListener implements Listener {

    private PluginService pluginService;

    private final List<Revisor> revisorTextList = new ArrayList<>();
    private final List<Revisor> revisorCmdList = new ArrayList<>();

    public RevisorListener(PluginService pluginService) {
        this.pluginService = pluginService;

        loadCmdRevisor(
                new BlockCmdRevisor(pluginService, "commands-module.block"),
                new ConditionRevisor(pluginService, "commands-module.conditions"));

        loadTextRevisors(
                new BotRevisor(pluginService, "bot-response"),
                new MentionRevisor(pluginService, "mentions"),
                new WordRevisor(pluginService, "words-module"),
                new LinkRevisor(pluginService, "link-module"),
                new FloodRevisor(pluginService, "anti-flood"),
                new FirstWordRevisor(pluginService, "first-mayus-module"),
                new CapsRevisor(pluginService, "caps-module"),
                new DotRevisor(pluginService, "dot-module"));

    }

    private void loadTextRevisors(Revisor... listRevisors) {
        revisorTextList.addAll(Arrays.asList(listRevisors));
    }

    private void loadCmdRevisor(Revisor... listCmdRevisors){
        revisorCmdList.addAll(Arrays.asList(listCmdRevisors));
    }

    @EventHandler
    public void onRevisor(TextRevisorEvent textRevisorEvent) {
        
            List<Revisor> revisorList;
            switch (textRevisorEvent.getRevisorType()){

                case TEXT:
                    revisorList = revisorTextList;
                    break;

                case COMMAND:
                    revisorList = revisorCmdList;
                    break;

                default:
                    return;
            }


            String message = textRevisorEvent.getMessage();
            if (textRevisorEvent.getRevisorExcepcions().contains("ALL")){
                return;
            }

            for (Revisor revisor : revisorList) {

                if (textRevisorEvent.getRevisorExcepcions().contains(revisor.getClass().getName().split("\\.")[5])) {
                    continue;
                }

                if (!revisor.isEnabled()){
                    continue;
                }

                message = revisor.revisor(textRevisorEvent.getPlayer(), message);

                if (message == null) {
                    textRevisorEvent.setCancelled(true);
                    return;
                }

              }

            textRevisorEvent.setMessageRevised(message);
    }
}
