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

                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".first-join.message.type") == null) {
                    jqData.setFirstJoinType(null);
                } else {
                    jqData.setFirstJoinType(formatsFile.getString("join-and-quit.formats." + dataRanks + ".first-join.message.type"));
                }

                // FirstJoin Format
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".first-join.message") == null) {
                    jqData.setFirstJoinFormat(null);
                } else {
                    jqData.setFirstJoinFormat(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.message"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".first-join.motd").isEmpty()) {
                    jqData.setFirstJoinMotdList(null);
                } else {
                    jqData.setFirstJoinMotdList(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.motd"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".first-join.commands").isEmpty()) {
                    jqData.setFirstJoinActions(null);
                } else {
                    jqData.setFirstJoinActions(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.commands"));
                }

            }
            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".join") != null) {

                // Join Format:
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".join.message.format") == null) {
                    jqData.setJoinType(null);
                } else {
                    jqData.setJoinType(formatsFile.getString("join-and-quit.formats." + dataRanks + ".join.message.type"));
                }

                // Join Format:
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".join.message.format") == null) {
                    jqData.setJoinFormat(null);
                } else {
                    jqData.setJoinFormat(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.message.format"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".join.motd").isEmpty()) {
                    jqData.setJoinMotdList(null);
                } else {
                    jqData.setJoinMotdList(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.motd"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".join.commands").isEmpty()) {
                    jqData.setJoinActions(null);
                } else {
                    jqData.setJoinActions(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.commands"));
                }

            }

            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".quit") != null) {

                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".quit.message.type") == null) {
                    jqData.setQuitType(null);
                } else {
                    jqData.setQuitType(formatsFile.getString("join-and-quit.formats." + dataRanks + ".quit.message.type"));
                }

                // Quit Format:
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".quit.message") == null) {
                    jqData.setQuitFormat(null);
                } else {
                    jqData.setQuitFormat(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".quit.message"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".quit.commands").isEmpty()) {
                    jqData.setQuitActions(null);
                } else {
                    jqData.setQuitActions(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".quit.commands"));
                }

            }

            jqFormatMP.put(dataRanks, jqData);
        }
    }
}
