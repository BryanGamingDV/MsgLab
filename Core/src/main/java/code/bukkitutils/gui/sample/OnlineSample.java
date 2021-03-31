package code.bukkitutils.gui.sample;

import code.PluginService;
import code.bukkitutils.RunnableManager;
import code.bukkitutils.gui.GuiSample;
import code.bukkitutils.gui.manager.GuiData;
import code.bukkitutils.gui.manager.GuiManager;
import code.bukkitutils.pages.PageUUIDCreator;
import code.data.UserData;
import code.managers.player.PlayerStatic;
import code.utils.Configuration;
import code.utils.string.StringUtils;
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

        GuiManager guiManager = pluginService.getManagingCenter().getGuiManager();
        Configuration command = pluginService.getFiles().getCommand();
        Configuration message = pluginService.getFiles().getMessages();

        PageUUIDCreator pageUUIDCreator = new PageUUIDCreator(getOnlinePlayers());
        listPlayers = pageUUIDCreator.getHashMap().get(page);

        String titlename = StringUtils.setColor(command.getString("commands.msg-online.title")
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

            name = ChatColor.RESET + PlayerStatic.convertText(player, command.getString("commands.msg-online.player.title")
                    .replace("%playername%", player.getName())
                    .replace("%number%", String.valueOf(number)));

            if (uuid.equals(sender)) {
                lore = Collections.singletonList(message.getColoredString("error.gui.message"));
            } else {
                lore = command.getStringList("commands.msg-online.player.lore");
                lore.replaceAll(text -> PlayerStatic.convertText(player, text));
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
        UserData userData = pluginService.getCache().getUserDatas().get(player.getUniqueId());

        ItemMeta item = event.getCurrentItem().getItemMeta();
        RunnableManager runnableManager = pluginService.getManagingCenter().getRunnableManager();
        Configuration command = pluginService.getFiles().getCommand();
        GuiManager guiManager = pluginService.getManagingCenter().getGuiManager();

        String previousName = ChatColor.RESET + StringUtils.setColor(command.getString("commands.msg-online.previous-page.title"));
        String nextName = ChatColor.RESET + StringUtils.setColor(command.getString("commands.msg-online.next-page.title"));

        player.closeInventory();

        if (item.getDisplayName().equalsIgnoreCase(previousName)) {
            userData.setChangeInv(true);
            userData.changePage(userData.getPage() - 1);
            guiManager.openInventory(player.getUniqueId(), "online", userData.getPage());
            userData.setChangeInv(false);
        }

        if (item.getDisplayName().equalsIgnoreCase(nextName)) {
            userData.setChangeInv(true);
            userData.changePage(userData.getPage() + 1);
            guiManager.openInventory(player.getUniqueId(), "online", userData.getPage());
            userData.setChangeInv(false);
        }

        for (UUID uuid : listPlayers) {
            String onlineName = Bukkit.getPlayer(uuid).getName();

            if (item.getDisplayName().contains(onlineName)) {
                runnableManager.sendCommand(player, "msg " + onlineName + " Hello!");
            }
        }
    }

    public List<UUID> getOnlinePlayers() {
        List<UUID> uuidList = new ArrayList<>();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            uuidList.add(player.getUniqueId());
        }
        
        return uuidList;
    }
    
}
