package net.teamuni.dailyreward.event;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class DragEvent implements Listener {

    @EventHandler
    public void dragEvent(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return;
        event.setCancelled(true);
    }
}
