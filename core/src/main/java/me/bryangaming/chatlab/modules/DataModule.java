package me.bryangaming.chatlab.modules;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.api.Module;
import me.bryangaming.chatlab.data.JQData;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.text.TextUtils;

import java.util.Collections;
import java.util.List;
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

            // Auth
            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".auth") != null) {

                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".auth.message.type") == null) {
                    jqData.setFirstJoinType(null);
                } else {
                    jqData.setFirstJoinType(formatsFile.getString("join-and-quit.formats." + dataRanks + ".auth.message.type"));
                }

                // FirstJoin Format
                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".auth.message.formats").isEmpty()) {
                    jqData.setFirstJoinFormat(null);
                } else {
                    jqData.setFirstJoinFormat(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".auth.message.formats"));
                }

                String oldFirstJoinFormat = formatsFile.getString("join-and-quit.formats." + dataRanks + ".auth.message");

                if (formatsFile.isString("join-and-quit.formats." + dataRanks + ".auth.message")){
                    if (TextUtils.equalsIgnoreCaseOr(oldFirstJoinFormat, "none", "silent")){
                        jqData.setJoinType(oldFirstJoinFormat);
                        jqData.setJoinFormat(Collections.singletonList(""));
                    }else{
                        jqData.setJoinType("none");
                        jqData.setJoinFormat(Collections.singletonList(oldFirstJoinFormat));
                    }
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".auth.motd").isEmpty()) {
                    jqData.setFirstJoinMotdList(null);
                } else {
                    jqData.setFirstJoinMotdList(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".auth.motd"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".auth.actions").isEmpty()) {
                    jqData.setFirstJoinActions(null);
                } else {
                    jqData.setFirstJoinActions(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".auth.actions"));
                }



            }

            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".first-join") != null) {

                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".first-join.message.type") == null) {
                    jqData.setFirstJoinType(null);
                } else {
                    jqData.setFirstJoinType(formatsFile.getString("join-and-quit.formats." + dataRanks + ".first-join.message.type"));
                }

                // FirstJoin Format
                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".first-join.message.formats").isEmpty()) {
                    jqData.setFirstJoinFormat(null);
                } else {
                    jqData.setFirstJoinFormat(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.message.formats"));
                }

                String oldFirstJoinFormat = formatsFile.getString("join-and-quit.formats." + dataRanks + ".first-join.message");

                if (formatsFile.isString("join-and-quit.formats." + dataRanks + ".first-join.message")){
                    if (TextUtils.equalsIgnoreCaseOr(oldFirstJoinFormat, "none", "silent")){
                        jqData.setJoinType(oldFirstJoinFormat);
                        jqData.setJoinFormat(Collections.singletonList(""));
                    }else{
                        jqData.setJoinType("none");
                        jqData.setJoinFormat(Collections.singletonList(oldFirstJoinFormat));
                    }
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".first-join.motd").isEmpty()) {
                    jqData.setFirstJoinMotdList(null);
                } else {
                    jqData.setFirstJoinMotdList(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.motd"));
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".first-join.actions").isEmpty()) {
                    jqData.setFirstJoinActions(null);
                } else {
                    jqData.setFirstJoinActions(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".first-join.actions"));
                }



            }
            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".join") != null) {

                // Join Format:
                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".join.message.type") == null) {
                    jqData.setJoinType(null);
                } else {
                    jqData.setJoinType(formatsFile.getString("join-and-quit.formats." + dataRanks + ".join.message.type"));
                }
                // Join Format:
                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".join.message.formats").isEmpty()) {
                    jqData.setJoinFormat(null);
                } else {
                    jqData.setJoinFormat(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.message.formats"));
                }

                String oldJoinFormat = formatsFile.getString("join-and-quit.formats." + dataRanks + ".join.message");

                if (formatsFile.isString("join-and-quit.formats." + dataRanks + ".join.message")){
                    if (TextUtils.equalsIgnoreCaseOr(oldJoinFormat, "none", "silent")){
                        jqData.setJoinType(oldJoinFormat);
                        jqData.setJoinFormat(Collections.singletonList(""));
                    }else{
                        jqData.setJoinType("none");
                        jqData.setJoinFormat(Collections.singletonList(oldJoinFormat));
                    }
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".join.motd").isEmpty()) {
                    jqData.setJoinMotdList(null);
                } else {
                    jqData.setJoinMotdList(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.motd"));
                }


                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".join.actions").isEmpty()) {
                    jqData.setJoinActions(null);
                } else {
                    jqData.setJoinActions(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".join.actions"));
                }

            }

            if (formatsFile.getConfigurationSection("join-and-quit.formats." + dataRanks + ".quit") != null) {

                if (formatsFile.getString("join-and-quit.formats." + dataRanks + ".quit.message.type") == null) {
                    jqData.setQuitType(null);
                } else {
                    jqData.setQuitType(formatsFile.getString("join-and-quit.formats." + dataRanks + ".quit.message.type"));
                }

                // Quit Format:
                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".quit.message.formats").isEmpty()) {
                    jqData.setQuitFormat(null);
                } else {
                    jqData.setQuitFormat(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".quit.message.formats"));
                }

                String oldQuitFormat = formatsFile.getString("join-and-quit.formats." + dataRanks + ".quit.message");

                if (formatsFile.isString("join-and-quit.formats." + dataRanks + ".quit.message")){
                    if (TextUtils.equalsIgnoreCaseOr(oldQuitFormat, "none", "silent")){
                        jqData.setJoinType(oldQuitFormat);
                        jqData.setJoinFormat(Collections.singletonList(""));
                    }else{
                        jqData.setJoinType("none");
                        jqData.setJoinFormat(Collections.singletonList(oldQuitFormat));
                    }
                }

                if (formatsFile.getStringList("join-and-quit.formats." + dataRanks + ".quit.actions").isEmpty()) {
                    jqData.setQuitActions(null);
                } else {
                    jqData.setQuitActions(formatsFile.getColoredStringList("join-and-quit.formats." + dataRanks + ".quit.actions"));
                }

            }

            jqFormatMP.put(dataRanks, jqData);
        }
    }
}
