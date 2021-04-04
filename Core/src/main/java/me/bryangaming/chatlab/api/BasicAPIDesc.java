package me.bryangaming.chatlab.api;

import java.util.List;
import java.util.UUID;

public interface BasicAPIDesc {

    /**
     * To check if the target is ignoring that player.
     *
     * @param targetPlayer  The player that want to ignore.
     * @param playerIgnored The player that you want to ignore.
     */
    default boolean isPlayerIgnored(UUID targetPlayer, UUID playerIgnored) {
        return false;
    }


    /**
     * To watch all players ignored of that player.
     *
     * @param uuid The player that you want to watch all ignored players for him.
     */
    default List<String> getIgnoredPlayers(UUID uuid) {
        return null;
    }

    /**
     * Get all players using the socialspy.
     */
    default List<String> getSpyList() {
        return null;
    }

    /**
     * Get all players the msg disabled.
     */
    default List<String> getMsgToggleList() {
        return null;
    }

    /**
     * To watch the replied player of that player.
     *
     * @param uuid The uuid that you want to watch the replied player.
     * @throws NullPointerException If player doesn't ignoring a player.
     */
    default UUID getRepliedPlayer(UUID uuid) {
        return null;
    }

    /**
     * Get the plugin description.
     */
    default String getDescription() {
        return null;
    }


    /**
     * Get the plugin name.
     */
    default String getPluginName() {
        return null;
    }

    /**
     * Get the version of the plugin.
     */
    default String getPluginVersion() {
        return null;
    }

    /**
     * Get the author of the plugin.
     */
    default String getPluginAuthor() {
        return null;
    }

}
