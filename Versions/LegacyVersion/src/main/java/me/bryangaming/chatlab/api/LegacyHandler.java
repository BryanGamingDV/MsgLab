package me.bryangaming.chatlab.api;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class LegacyHandler implements NMSHandler {

    @Override
    public void addHead(Inventory inventory, Player owner, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

        itemMeta.setOwner(owner.getName());
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        inventory.addItem(itemStack);
    }
}
