package net.teamuni.dailyreward.event;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class DragEvent implements Listener {

    @EventHandler
    public void dragEvent(InventoryDragEvent event) { //인벤토리 드래그이벤트가 발생했을때,
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return; //인벤토리의 타이틀이 "ChatColor.GREEN + "출석체크 GUI"" 가 아니면, 드래그이벤트 함수를 끝냄.
        event.setCancelled(true); //드래그이벤트를 취소함
    }
}
