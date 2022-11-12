package net.teamuni.dailyreward.gui;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DailyRewardGui {
    private final DailyReward main;
    public DailyRewardGui(DailyReward instance){ //DailyRewardGui 생성자
        this.main = instance;
    }
    private void loadItems(Inventory gui, Player player) { //getRewards 메소드에서 아이템들을 읽어와 인벤토리에 설정해주는 메소드
        Map<Integer, ItemStack> dailyItems = new HashMap<>(main.getRewardManager().getRewards(player.getUniqueId()));
        for (Map.Entry<Integer, ItemStack> dailyItem : dailyItems.entrySet()) {
            gui.setItem(dailyItem.getKey(), dailyItem.getValue());
        }
    }
    public void openGui(Player player) { //플레이어에게 출석체크 GUI 를 열게 해주는 메소드
        Inventory dailyRewardGui = Bukkit.createInventory(null, 54, ChatColor.GREEN + "출석체크 GUI");
        loadItems(dailyRewardGui, player);
        player.openInventory(dailyRewardGui);
        main.getConfigManager().playSound(player, "Gui_Open_Sound");
    }
}
