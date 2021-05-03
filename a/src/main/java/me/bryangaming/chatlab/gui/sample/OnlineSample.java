package me.bryangaming.chatlab.gui.sample;

import me.bryangaming.chatlab.PluginService;
import me.bryangaming.chatlab.data.UserData;
import me.bryangaming.chatlab.gui.manager.GuiData;
import me.bryangaming.chatlab.gui.manager.GuiSample;
import me.bryangaming.chatlab.managers.GuiManager;
import me.bryangaming.chatlab.managers.SenderManager;
import me.bryangaming.chatlab.utils.Configuration;
import me.bryangaming.chatlab.utils.pages.PageUUIDCreator;
import me.bryangaming.chatlab.utils.string.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OnlineSample implements GuiSample {

    private final PluginService pluginService;
    private List<UUID> listPlayers;

    public OnlineSample(PluginService pluginService) {
        this.pluginService = pluginService;
    }


    public GuiData getPage(UUID sender, Integer page) {

        GuiManager guiManager = pluginService.getPlayerManager().getGuiManager();
        Configuration command = pluginService.getFiles().getCommandFile();
        Configuration message = pluginService.getFiles().getMessagesFile();

        PageUUIDCreator pageUUIDCreator = new PageUUIDCreator(getOnlinePlayers());
        listPlayers = pageUUIDCreator.getHashMap().get(page);

        String titlename = TextUtils.setColor(command.getString("commands.msg-online.title")
                .replace("%page%", String.valueOf(page + 1))
                .replace("%max%", String.valueOf(pageUUIDCreator.getMaxPage())));

        guiManager.createInventory("online", titlename, 5);
        GuiData inventory = guiManager.getInventory("online");

        if (inventory.containsItems()) {
            inventory.reset();
        }

        String name;
        List<String> lore;
        int number = 1;

        for (UUID uuid : listPlayers) {
            Player player = Bukkit.getPlayer(uuid);

            name = ChatColor.RESET + TextUtils.convertText(player, command.getString("commands.msg-online.player.title")
                    .replace("%playername%", player.getName())
                    .replace("%number%", String.valueOf(number)));

            if (uuid.equals(sender)) {
                lore = Collections.singletonList(message.getColoredString("error.gui.message"));
            } else {
                lore = command.getStringList("commands.msg-online.player.lore");
                lore.replaceAll(text -> TextUtils.convertText(player, text));
            }


            inventory.addHead(player, name, lore);
            number++;
        }

        int pagegui = inventory.getSize();

        String previousName = ChatColor.RESET + command.getColoredString("commands.msg-online.previous-page.title");
        String nextName = ChatColor.RESET + command.getColoredString("commands.msg-online.next-page.title");

        List<String> previousLore = command.getColoredStringList("commands.msg-online.previous-page.lore");
        List<String> nextLore = command.getColoredStringList("commands.msg-online.next-page.lore");

        if (Bukkit.getOnlinePlayers().size() > 36) {
            if (page > 0) {
                inventory.setItem(pagegui - 1, command.getColoredString("commands.msg-online.next-page.id_name"), nextName, previousLore);
            } else {
                if (listPlayers.size() <= 27) {
                    inventory.setItem(pagegui - 10, command.getColoredString("commands.msg-online.previous-page.id_name"), previousName, previousLore);
                }
                inventory.setItem(pagegui - 1, command.getColoredString("commands.msg-online.next-page.id_name"), nextName, nextLore);
            }
        }

        return inventory;
    }

    public void getClickEvent(InventoryClickEvent event) {

        if (event.getClick().isLeftClick()) {
            event.setCancelled(true);
            return;
        }
        HumanEntity player = event.getWhoClicked();
        player.closeInventory();

        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        ItemMeta item = event.getCurrentItem().getItemMeta();
        SenderManager senderManager = pluginService.getPlayerManager().getSender();
        Configuration command = pluginService.getFiles().getCommandFile();

        String previousName = ChatColor.RESET + command.getColoredString("commands.msg-online.previous-page.title");
        String nextName = ChatColor.RESET + command.getColoredString("commands.msg-online.next-page.title");

        if (item.getDisplayName().equalsIgnoreCase(previousName)) {
            userData.setChangeInv(true);
            userData.changePage(userData.getPage() - 1);
            senderManager.openInventory(player, "online", userData.getPage());
            userData.setChangeInv(false);
        }

        if (item.getDisplayName().equalsIgnoreCase(nextName)) {
            userData.setChangeInv(true);
            userData.changePage(userData.getPage() + 1);
            senderManager.openInventory(player, "online", userData.getPage());
            userData.setChangeInv(false);
        }

        for (UUID uuid : listPlayers) {
            String onlineName = Bukkit.getPlayer(uuid).getName();

            if (item.getDisplayName().contains(onlineName)) {
                senderManager.sendCommand(player, "msg " + onlineName + " Hello!");
            }
        }
    }

    public List<UUID> getOnlinePlayers() {
        List<UUID> uuidList = new ArrayList<>();
        Bukkit.getServer().getOnlinePlayers().forEach(player -> uuidList.add(player.getUniqueId()));
        return uuidList;
    }

}
