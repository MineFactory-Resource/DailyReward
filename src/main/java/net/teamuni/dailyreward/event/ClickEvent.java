package net.teamuni.dailyreward.event;

import net.teamuni.dailyreward.Dailyreward;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.UUID;

public class ClickEvent implements Listener {
    private final Dailyreward main;

    public ClickEvent(Dailyreward instance) {
        this.main = instance;
    }
    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        String key = main.getRewardManager().getDayBySlot(event.getSlot());
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        if (main.getRewardManager().getKeyDay(key) > main.getPlayerDataManager().getPlayerCumulativeDate(uuid)) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.WHITE + "아직 해당 일차의 보상을 수령할 수 없습니다!");
            player.closeInventory();
            return;
        }
        String rewardName = main.getRewardManager().getSection(key).getString("name");
        List<String> rewardList = main.getPlayerDataManager().getPlayerReceivedRewardsList(uuid);
        if (rewardList.contains(key)) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 이미 수령하셨습니다!");
            player.closeInventory();
            return;
        }
        main.getDailyRewardCommand().executeCommand(player, key);
        main.getPlayerDataManager().updatePlayerRewardList(uuid, key, rewardList);
        player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 수령했습니다!");
        player.closeInventory();
    }
}
