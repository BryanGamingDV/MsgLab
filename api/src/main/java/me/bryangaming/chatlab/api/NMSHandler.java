package me.bryangaming.chatlab.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public interface NMSHandler {

    void addHead(Inventory inventory, Player owner, String name, List<String> lore);
}
