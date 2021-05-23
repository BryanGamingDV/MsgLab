package me.bryangaming.chatlab.common.wrapper;

import java.util.List;

public interface SenderWrapper {

    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    String getName();

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     */
    void sendMessage(String message);

    /**
     * Sends this sender multiple messages
     *
     * @param messages An list of messages to be displayed
     */
    void sendMessage(List<String> messages);

    /**
     * Gets the value of the specified permission, if set.
     * <p>
     * If a permission override is not set on this object, the default value
     * of the permission will be returned.
     *
     * @param permission Name of the permission
     * @return Value of the permission
     */
    boolean hasPermission(String permission);



}
