package me.bryangaming.chatlab.listeners;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.events.revisor.TextRevisorEvent;
import me.bryangaming.chatlab.api.revisor.Revisor;
import me.bryangaming.chatlab.revisor.commands.BlockRevisor;
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
                new BlockRevisor(pluginService),
                new ConditionRevisor(pluginService));

        loadTextRevisors(
                new BotRevisor(pluginService),
                new MentionRevisor(pluginService),
                new WordRevisor(pluginService),
                new FirstWordRevisor(pluginService),
                new FloodRevisor(pluginService),
                new CapsRevisor(pluginService),
                new LinkRevisor(pluginService),
                new DotRevisor(pluginService));

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

            String message = "";
            if (textRevisorEvent.getRevisorExcepcions().contains("ALL")){
                return;
            }

            for (Revisor revisor : revisorList) {

                pluginService.getPlugin().getLogger().info(revisor.getClass().getName());

                if (textRevisorEvent.getRevisorExcepcions().contains(revisor.getClass().getName())) {
                    continue;
                }

                message = revisor.revisor(textRevisorEvent.getPlayer(), textRevisorEvent.getMessage());

                if (message == null) {
                    textRevisorEvent.setCancelled(true);
                    return;
                }
            }

            textRevisorEvent.setMessageRevised(message);
    }
}
