package me.bryangaming.chatlab.modules;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.utils.Configuration;

import java.util.Map;

public class DataModule implements Module {

    private PluginService pluginService;

    public DataModule(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public void start() {

        Configuration utils = pluginService.getFiles().getFormatsFile();
        Map<String, JQData> jqFormatMP = pluginService.getCache().getJQFormats();

        if (jqFormatMP.keySet().size() > 0) {
            jqFormatMP.clear();
        }

        for (String dataRanks : utils.getConfigurationSection("lobby.formats").getKeys(false)) {
            JQData jqData = new JQData(dataRanks);

            if (utils.getConfigurationSection("lobby.formats." + dataRanks + ".first-join") != null) {

                // FirstJoin Format
                if (utils.getString("lobby.formats." + dataRanks + ".first-join.message") == null) {
                    jqData.setFirstJoinFormat("none");
                } else {
                    jqData.setFirstJoinFormat(utils.getColoredString("lobby.formats." + dataRanks + ".first-join.message"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".first-join.motd").isEmpty()) {
                    jqData.setFirstJoinMotdList(null);
                } else {
                    jqData.setFirstJoinMotdList(utils.getColoredStringList("lobby.formats." + dataRanks + ".first-join.motd"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".first-join.commands").isEmpty()) {
                    jqData.setFirstJoinCommands(null);
                } else {
                    jqData.setFirstJoinCommands(utils.getColoredStringList("lobby.formats." + dataRanks + ".first-join.commands"));
                }

            }
            if (utils.getConfigurationSection("lobby.formats." + dataRanks + ".join") != null) {

                // Join Format:
                if (utils.getString("lobby.formats." + dataRanks + ".join.message") == null) {
                    jqData.setJoinFormat("none");
                } else {
                    jqData.setJoinFormat(utils.getColoredString("lobby.formats." + dataRanks + ".join.message"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".join.motd").isEmpty()) {
                    jqData.setJoinMotdList(null);
                } else {
                    jqData.setJoinMotdList(utils.getColoredStringList("lobby.formats." + dataRanks + ".join.motd"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".join.commands").isEmpty()) {
                    jqData.setJoinCommands(null);
                } else {
                    jqData.setJoinCommands(utils.getColoredStringList("lobby.formats." + dataRanks + ".join.commands"));
                }

            }

            if (utils.getConfigurationSection("lobby.formats." + dataRanks + ".quit") != null) {

                // Quit Format:
                if (utils.getString("lobby.formats." + dataRanks + ".quit.message") == null) {
                    jqData.setQuitFormat("none");
                } else {
                    jqData.setQuitFormat(utils.getColoredString("lobby.formats." + dataRanks + ".quit.message"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".quit.commands").isEmpty()) {
                    jqData.setQuitCommands(null);
                } else {
                    jqData.setQuitCommands(utils.getColoredStringList("lobby.formats." + dataRanks + ".quit.commands"));
                }

            }

            jqFormatMP.put(dataRanks, jqData);
        }
    }
}
