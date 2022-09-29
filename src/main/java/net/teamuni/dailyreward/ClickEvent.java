package net.teamuni.dailyreward;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ClickEvent implements Listener {
    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        if (!e.getInventory().equals(RewardManager.DailyRewardGui)) return;
        e.setCancelled(true);

    }
    @EventHandler
    public void dragEvent(InventoryDragEvent e) {
        if (!e.getInventory().equals(RewardManager.DailyRewardGui)) return;
        e.setCancelled(true);
    }
}
