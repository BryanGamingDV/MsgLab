package me.bryangaming.chatlab.modules;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.utils.Configuration;

import java.util.Map;

public class DataModule implements Module {

    private final PluginService pluginService;

    public DataModule(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @Override
    public void start() {

        Configuration formatsFile = pluginService.getFiles().getFormatsFile();
        Map<String, JQData> jqFormatMP = pluginService.getCache().getJQFormats();

        if (jqFormatMP.keySet().size() > 0) {
            jqFormatMP.clear();
        }

        for (String dataRanks : formatsFile.getConfigurationSection("join-and-quit.formats").getKeys(false)) {
            JQData jqData = new JQData(dataRanks);

            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".first-join") != null) {

                // FirstJoin Format
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".first-join.message") == null) {
                    jqData.setFirstJoinFormat("none");
                } else {
                    jqData.setFirstJoinFormat(formatsFile.getColoredString("join-and-quit.formats." + dataRanks + ".first-join.message"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".first-join.motd").isEmpty()) {
                    jqData.setFirstJoinMotdList(null);
                } else {
                    jqData.setFirstJoinMotdList(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.motd"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".first-join.commands").isEmpty()) {
                    jqData.setFirstJoinCommands(null);
                } else {
                    jqData.setFirstJoinCommands(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.commands"));
                }

            }
            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".join") != null) {

                // Join Format:
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".join.message") == null) {
                    jqData.setJoinFormat("none");
                } else {
                    jqData.setJoinFormat(formatsFile.getColoredString("join-and-quit.formats." + dataRanks + ".join.message"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".join.motd").isEmpty()) {
                    jqData.setJoinMotdList(null);
                } else {
                    jqData.setJoinMotdList(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.motd"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".join.commands").isEmpty()) {
                    jqData.setJoinCommands(null);
                } else {
                    jqData.setJoinCommands(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.commands"));
                }

            }

            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".quit") != null) {

                // Quit Format:
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".quit.message") == null) {
                    jqData.setQuitFormat("none");
                } else {
                    jqData.setQuitFormat(formatsFile.getColoredString("join-and-quit.formats." + dataRanks + ".quit.message"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".quit.commands").isEmpty()) {
                    jqData.setQuitCommands(null);
                } else {
                    jqData.setQuitCommands(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".quit.commands"));
                }

            }

            jqFormatMP.put(dataRanks, jqData);
        }
    }
}
