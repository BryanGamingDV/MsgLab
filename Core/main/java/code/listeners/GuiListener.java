package code.listeners;

import code.PluginService;
import code.bukkitutils.gui.OnlineSample;
import code.data.UserData;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener{

    private PluginService pluginService;

    public GuiListener(PluginService pluginService){
        this.pluginService = pluginService;
    }


    @EventHandler
    public void onOpenGUI(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();

        UserData userData = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        OnlineSample onlineSample = pluginService.getManagingCenter().getGuiManager().getSampleManager().getOnlineSample();

        if (!userData.isGUISet()){
            return;
        }

        if (event.getClickedInventory() == null){
            return;
        }

        if (event.getCurrentItem() == null){
            return;
        }

        if (event.getCurrentItem().getType() == Material.AIR){
            return;
        }

        if (!event.getCurrentItem().hasItemMeta()){
            return;
        }

        if (userData.equalsGUIGroup("online")){
            onlineSample.getOnlineClickEvent(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){

        HumanEntity player = event.getPlayer();
        UserData userData = pluginService.getCache().getPlayerUUID().get(player.getUniqueId());

        if (userData.isChangingPage()){
            return;
        }

        if (userData.isGUISet()){
            userData.setGUIGroup("default");
        }

        if (userData.getPage() > 0){
            userData.changePage(0);
        }
    }
}
