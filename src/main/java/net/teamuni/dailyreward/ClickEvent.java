package net.teamuni.dailyreward;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class ClickEvent implements Listener {
    private final Inventory inventory;
    public ClickEvent(RewardManager rewardManager){
        this.inventory = rewardManager.DailyRewardGui;
    }


    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        if(!e.getInventory().equals(inventory)) return;
        e.setCancelled(true);
    }
    @EventHandler
    public void dragEvent(InventoryDragEvent e) {
        if(!e.getInventory().equals(inventory)) return;
        e.setCancelled(true);
    }
}
