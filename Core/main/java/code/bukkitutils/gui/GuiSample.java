package code.bukkitutils.gui;

import code.bukkitutils.gui.manager.GuiData;

import java.util.UUID;

public interface GuiSample {

    /**
     * A sample of the gui.
     *
     * @param sender The sender, obviously.
     * @param page The page that the sender want to open.
     */
    GuiData getPage(UUID sender, Integer page);
}
