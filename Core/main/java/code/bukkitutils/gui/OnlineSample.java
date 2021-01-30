package code.bukkitutils.gui;

import code.Manager;
import code.bukkitutils.RunnableManager;
import code.bukkitutils.gui.manager.GuiData;
import code.bukkitutils.gui.manager.GuiManager;
import code.bukkitutils.other.PageUUIDManager;
import code.cache.UserData;
import code.methods.player.PlayerStatic;
import code.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OnlineSample implements GuiSample{

    private Manager manager;

    private GuiData inventory;
    private List<UUID> listPlayers;

    public OnlineSample(Manager manager) {
        this.manager = manager;
    }

    public GuiData getPage(UUID sender, Integer page) {

        GuiManager guiManager = manager.getManagingCenter().getGuiManager();
        Configuration command = manager.getFiles().getCommand();
        Configuration message = manager.getFiles().getMessages();

        PageUUIDManager pageUUIDManager = new PageUUIDManager(getOnlinePlayers());
        listPlayers = pageUUIDManager.getHash().get(page);

        String titlename = PlayerStatic.setColor(command.getString("commands.msg-online.title")
                .replace("%page%", String.valueOf(page + 1))
                .replace("%max%", String.valueOf(pageUUIDManager.getMaxPage())));

        guiManager.createInventory("online", titlename , 5);

        inventory = guiManager.getInventory("online");

        if (inventory.containsItems()) {
            inventory.reset();
        }
        String[] args = new String[3];

        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }

        String name;
        List<String> lore;
        int number = 1;

        for (UUID uuid : listPlayers) {
            Player player = Bukkit.getPlayer(uuid);

            name = ChatColor.RESET + PlayerStatic.setFormat(player, command.getString("commands.msg-online.player.title")
                    .replace("%playername%", player.getName())
                    .replace("%number%", String.valueOf(number)));

            if (uuid.equals(sender)) {
                List<String> newlore = Collections.singletonList(message.getString("error.gui.message"));
                lore = PlayerStatic.setColorList(newlore);
            } else {
                lore = PlayerStatic.setPlaceholdersList(player, command.getStringList("commands.msg-online.player.lore"), true);
            }

            inventory.addHead(player, name, lore);
            number++;
        }


        int pagegui = inventory.getSize();

        String previousName = ChatColor.RESET + PlayerStatic.setColor(command.getString("commands.msg-online.previous-page.title"));
        String nextName = ChatColor.RESET + PlayerStatic.setColor(command.getString("commands.msg-online.next-page.title"));

        List<String> previousLore = PlayerStatic.setColorList(command.getStringList("commands.msg-online.previous-page.lore"));
        List<String> nextLore = PlayerStatic.setColorList(command.getStringList("commands.msg-online.next-page.lore"));

        if (Bukkit.getOnlinePlayers().size() > 36) {
            if (page > 0) {
                inventory.setItem(pagegui - 1, command.getString("commands.msg-online.next-page.id_name"), nextName, previousLore);
            } else {
                if (listPlayers.size() <= 27) {
                    inventory.setItem(pagegui - 10, command.getString("commands.msg-online.previous-page.id_name"), previousName, previousLore);
                }
                inventory.setItem(pagegui - 1, command.getString("commands.msg-online.next-page.id_name"), nextName, nextLore);
            }
        }
        return inventory;
    }

    public void getOnlineClickEvent(InventoryClickEvent event){

        if (event.getClick().isLeftClick()){
            event.setCancelled(true);
            return;
        }

        HumanEntity playersender = event.getWhoClicked();
        UserData userData = manager.getCache().getPlayerUUID().get(playersender.getUniqueId());

        ItemMeta item = event.getCurrentItem().getItemMeta();
        RunnableManager runnableManager = manager.getManagingCenter().getRunnableManager();
        Configuration command = manager.getFiles().getCommand();
        GuiManager guiManager = manager.getManagingCenter().getGuiManager();

        String previousName = ChatColor.RESET + PlayerStatic.setColor(command.getString("commands.msg-online.previous-page.title"));
        String nextName = ChatColor.RESET + PlayerStatic.setColor(command.getString("commands.msg-online.next-page.title"));

        if (item.getDisplayName().equalsIgnoreCase(previousName)){
            event.getWhoClicked().closeInventory();
            userData.changePage(userData.getPage() - 1);
            guiManager.openInventory(playersender.getUniqueId(), "online", userData.getPage());
        }

        if (item.getDisplayName().equalsIgnoreCase(nextName)){
            userData.setChangeInv(true);
            event.getWhoClicked().closeInventory();
            userData.changePage(userData.getPage() + 1);
            guiManager.openInventory(playersender.getUniqueId(), "online", userData.getPage());
            userData.setChangeInv(false);
        }

        for (UUID uuid : listPlayers) {
            Player player = Bukkit.getPlayer(uuid);

            if (item.getDisplayName().contains(player.getName())){
                event.getWhoClicked().closeInventory();
                runnableManager.sendCommand(event.getWhoClicked(), "msg " + player.getName() + " Hello!");
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
