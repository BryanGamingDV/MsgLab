package me.bryangaming.chatlab.gui.sample;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.gui.manager.GuiData;
import me.bryangaming.chatlab.gui.manager.GuiSample;
import me.bryangaming.chatlab.managers.GuiManager;
import me.bryangaming.chatlab.utils.Configuration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TagSample implements GuiSample {

    private final PluginService pluginService;
    private final Configuration filtersFile;

    public TagSample(PluginService pluginService) {
        this.pluginService = pluginService;
        this.filtersFile = pluginService.getFiles().getFiltersFile();
    }

    @Override
    public GuiData getPage(UUID sender, Integer page) {
        List<String> groupList = new ArrayList<>(filtersFile.getConfigurationSection("tags").getKeys(false));

        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        guiManager.createInventory("tag", filtersFile.getColoredString("tags.config.gui-format"), 5);


        GuiData inventory = guiManager.getInventory("tag");

        if (inventory.containsItems()) {
            inventory.reset();
        }

        String groupName = groupList.get(page);

        for (String items : filtersFile.getConfigurationSection("tags." + groupName + ".list").getKeys(false)){
            String itemName;

            if (!filtersFile.contains("tags." + groupName + ".list." + items + ".item_id")){
                itemName = filtersFile.getString("tags.config.default-item-id");
            }else{
                itemName = filtersFile.getString("tags." + groupName + ".list." + items + ".item_id");
            }

            String name = filtersFile.getColoredString("tags." + groupName + ".list." + items + ".name");
            List<String> lore = filtersFile.getColoredStringList("tags.config.item-lore");

            inventory.addItem(itemName, name, lore);

        }

        return inventory;
    }

    @Override
    public void getClickEvent(InventoryClickEvent event) {

        if (event.getClick().isLeftClick()) {
            event.setCancelled(true);
            return;
        }
        HumanEntity player = event.getWhoClicked();
        player.closeInventory();

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());
        ItemMeta item = event.getCurrentItem().getItemMeta();

        String group = userData.getGUIGroup(1);

        for (String items : filtersFile.getConfigurationSection("tags." + group + ".list").getKeys(false)){
            if (item.getDisplayName().equalsIgnoreCase(filtersFile.getString("tags." + group + ".list." + items))){

                String tagVariable = filtersFile.getString("tags." + group + ".list." + items + ".variable");

                if (!userData.gethashTags().containsKey(group)) {
                    userData.gethashTags().put(group, items);
                }else{
                    userData.gethashTags().replace(group, items);
                }
                pluginService.getPlayerManager().getSender().sendMessage(player, pluginService.getFiles().getMessagesFile().getString("tags.set")
                        .replace("%group%", items)
                        .replace("%tag%", tagVariable));
            }
        }
    }
}
