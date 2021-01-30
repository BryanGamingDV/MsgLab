package code.api;

import java.util.List;
import java.util.UUID;

public interface BasicAPI {

    /**
     * To check if the target is ignoring that player.
     *
     * @param targetPlayer  The player that want to ignore.
     * @param playerIgnored The player that you want to ignore.
     */
    static boolean isPlayerIgnored(UUID targetPlayer, UUID playerIgnored) {
        return false;
    }


    /**
     * To watch all players ignored of that player.
     *
     * @param uuid The player that you want to watch all ignored players for him.
     */
    static List<String> getIgnoredPlayers(UUID uuid) {
        return null;
    }

    /**
     * Get all players using the socialspy.
     */
    static List<String> getSpyList() {
        return null;
    }

    /**
     * Get all players the msg disabled.
     */
    static List<String> getMsgToggleList() {
        return null;
    }

    /**
     * To watch the replied player of that player.
     *
     * @param uuid The uuid that you want to watch the replied player.
     * @throws NullPointerException If player doesn't ignoring a player.
     */
    static UUID getRepliedPlayer(UUID uuid) {
        return null;
    }

    /**
     * Get the plugin description.
     */
    static String getDescription() {
        return null;
    }

    /**
     * Get the plugin name.
     */
    static String getPluginName() {
        return null;
    }

    /**
     * Get the version of the plugin.
     */
    static String getPluginVersion() {
        return null;
    }

    /**
     * Get the author of the plugin.
     */
    static String getPluginAuthor() {
        return null;
    }

}
