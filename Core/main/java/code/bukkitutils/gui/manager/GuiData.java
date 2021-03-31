package code.bukkitutils.gui.manager;

import bryangaming.code.api.LegacyHandler;
import bryangaming.code.api.NMSHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class GuiData {

    private final NMSHandler nmsHandler = new LegacyHandler();
    private final Inventory inv;

    public GuiData(String title, int size) {
        inv = Bukkit.createInventory(null, size, title);

    }

    public boolean containsItems() {
        return inv.getContents() != null;
    }

    public String getTitle(InventoryClickEvent event) {
        return event.getView().getTitle();
    }

    public void addItem(String item, String name, List<String> lore) {
        inv.addItem(createItem(Material.valueOf(item.toUpperCase()), name, lore));
    }

    public void addItems(ItemStack... itemStacks){
        for (ItemStack itemStack : itemStacks){
            inv.addItem(itemStack);
            return;
        }
    }
    public void addItem(String item, String name) {
        inv.addItem(createItem(Material.valueOf(item.toUpperCase()), name));
    }

    public void addHead(Player owner, String name, List<String> lore) {

        String version = Bukkit.getServer().getClass().getName();
        String versionname = version.split("\\.")[3].substring(1).replace("_", ".");

        if (versionname.startsWith("1.8") || versionname.startsWith("1.9") || versionname.startsWith("1.10") || versionname.startsWith("1.11") || versionname.startsWith("1.12") || versionname.startsWith("1.13")) {
            nmsHandler.addHead(inv, owner, name, lore);
            return;
        }

        inv.addItem(createHead(owner, name, lore));
    }

    public ItemStack createHead(Player owner, String name, List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();

        itemMeta.setOwningPlayer(owner);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        skull.setItemMeta(itemMeta);

        return skull;
    }

    public void setItem(int id, String item, String name, List<String> lore) {
        if (lore != null) {
            inv.setItem(id, createItem(Material.valueOf(item.toUpperCase()), name, lore));
        } else {
            inv.setItem(id, createItem(Material.valueOf(item.toUpperCase()), name));
        }
    }

    public int getSize() {
        return inv.getSize();
    }

    public void reset() {
        inv.clear();
    }

    public ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public Inventory build() {
        return inv;
    }
}
