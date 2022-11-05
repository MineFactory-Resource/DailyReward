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
    public DailyRewardGui(DailyReward instance){
        this.main = instance;
    }
    private void loadItems(Inventory gui, Player player) {
        Map<Integer, ItemStack> dailyItem = new HashMap<>(main.getRewardManager().getRewards(player.getUniqueId()));
        for (Map.Entry<Integer, ItemStack> dailyItems : dailyItem.entrySet()) {
            gui.setItem(dailyItems.getKey(), dailyItems.getValue());
        }
    }
    public void openGui(Player player) {
        Inventory dailyRewardGui = Bukkit.createInventory(null, 54, ChatColor.GREEN + "출석체크 GUI");
        loadItems(dailyRewardGui, player);
        player.openInventory(dailyRewardGui);
    }
}
