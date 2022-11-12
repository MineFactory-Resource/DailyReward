package net.teamuni.dailyreward.event;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.UUID;

public class ClickEvent implements Listener {
    private final DailyReward main;

    public ClickEvent(DailyReward instance) {
        this.main = instance;
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent event) { //인벤토리의 클릭이벤트가 발생했을때,
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return; //제목이 ChatColor.GREEN + "출석체크 GUI" 와 같지않으면 함수를 종료하고 같다면
        event.setCancelled(true); //이벤트를 취소하고, 아래의 코드들을 실행시켜주는 이벤트 메소드.
        if (event.getCurrentItem() == null) return;
        String key = main.getRewardFileManager().getDayBySlot(event.getSlot());
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        if (main.getRewardFileManager().getKeyDay(key) > main.getPlayerDataManager().getPlayerCumulativeDate(uuid)) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.WHITE + "아직 해당 일차의 보상을 수령할 수 없습니다!");
            player.closeInventory();
            main.getConfigManager().playSound(player, "Not_Receipt_Reward_Sound");
            return;
        }
        String rewardName = main.getRewardFileManager().loadConfigurationSection().getString(key + ".name");
        List<String> rewardList = main.getPlayerDataManager().getPlayerReceivedRewardsList(uuid);
        if (rewardList.contains(key)) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " +
                    ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 이미 수령하셨습니다!");
            player.closeInventory();
            main.getConfigManager().playSound(player, "Already_Received_Reward_Sound");
            return;
        }
        main.getDailyRewardCommand().executeCommand(player, key);
        main.getPlayerDataManager().updatePlayerRewardList(uuid, key, rewardList);
        player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 수령했습니다!");
        player.closeInventory();
        main.getConfigManager().playSound(player, "Receipt_Reward_Sound");
    }
}
