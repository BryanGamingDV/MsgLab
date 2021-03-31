package code.modules;

import code.CacheManager;
import code.PluginService;
import code.managers.jq.JQFormat;
import code.utils.Configuration;

import java.util.Map;

public class DataModule {

    private PluginService pluginService;

    public DataModule(PluginService pluginService){
        this.pluginService = pluginService;
        loadData();
    }

    public void loadData(){

        Configuration utils = pluginService.getFiles().getBasicUtils();
        Map<String, JQFormat> jqFormatMP = pluginService.getCache().getJQFormats();

        if (jqFormatMP.keySet().size() > 0){
            jqFormatMP.clear();
        }

        for (String dataRanks : utils.getConfigurationSection("lobby.formats").getKeys(false)){
            JQFormat jqFormat = new JQFormat();

            if (utils.getConfigurationSection("lobby.formats." + dataRanks + ".first-join") != null) {

                // FirstJoin Format
                if (utils.getString("lobby.formats." + dataRanks + ".first-join.message") == null) {
                    jqFormat.setFirstJoinFormat("none");
                } else {
                    jqFormat.setFirstJoinFormat(utils.getColoredString("lobby.formats." + dataRanks + ".first-join.message"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".first-join.motd").isEmpty()) {
                    jqFormat.setFirstJoinMotdList(null);
                } else {
                    jqFormat.setFirstJoinMotdList(utils.getColoredStringList("lobby.formats." + dataRanks + ".first-join.motd"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".first-join.commands").isEmpty()) {
                    jqFormat.setFirstJoinCommands(null);
                } else {
                    jqFormat.setFirstJoinCommands(utils.getColoredStringList("lobby.formats." + dataRanks + ".first-join.commands"));
                }

            }
            if (utils.getConfigurationSection("lobby.formats." + dataRanks + ".join") != null){

                // Join Format:
                if (utils.getString("lobby.formats." + dataRanks + ".join.message") == null) {
                    jqFormat.setJoinFormat("none");
                } else {
                    jqFormat.setJoinFormat(utils.getColoredString("lobby.formats." + dataRanks + ".join.message"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".join.motd").isEmpty()) {
                    jqFormat.setJoinMotdList(null);
                } else {
                    jqFormat.setJoinMotdList(utils.getColoredStringList("lobby.formats." + dataRanks + ".join.motd"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".join.commands").isEmpty()) {
                    jqFormat.setJoinCommands(null);
                } else {
                    jqFormat.setJoinCommands(utils.getColoredStringList("lobby.formats." + dataRanks + ".join.commands"));
                }

            }

            if (utils.getConfigurationSection("lobby.formats." + dataRanks + ".quit") != null){

                // Quit Format:
                if (utils.getString("lobby.formats." + dataRanks + ".quit.message") == null) {
                    jqFormat.setQuitFormat("none");
                } else {
                    jqFormat.setQuitFormat(utils.getColoredString("lobby.formats." + dataRanks + ".quit.message"));
                }

                if (utils.getStringList("lobby.formats." + dataRanks + ".quit.commands").isEmpty()) {
                    jqFormat.setQuitCommands(null);
                } else {
                    jqFormat.setQuitCommands(utils.getColoredStringList("lobby.formats." + dataRanks + ".quit.commands"));
                }

            }

            jqFormatMP.put(dataRanks, jqFormat);
        }
    }
}
