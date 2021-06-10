package me.bryangaming.chatlab.gui.manager;

import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public interface GuiSample {

    /**
     * A sample of the gui.
     *
     * @param sender The sender, obviously.
     * @param page   The page that the sender want to open.
     */
    GuiData getPage(UUID sender, Integer page);


    /**
     * When you click something of the event.
     *
     * @param event the click event.
     */
    void getClickEvent(InventoryClickEvent event);
}
