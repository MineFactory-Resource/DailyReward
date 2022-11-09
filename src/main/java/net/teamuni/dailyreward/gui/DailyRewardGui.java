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
    private final DailyReward main; //생성자 초기화
    public DailyRewardGui(DailyReward instance){ //DailyRewardGui 생성자
        this.main = instance;
    }
    private void loadItems(Inventory gui, Player player) {
        Map<Integer, ItemStack> dailyItem = new HashMap<>(main.getRewardManager().getRewards(player.getUniqueId()));
        //player의 UUID를 RewardManager 클래스의 getRewards 메소드 매개변수에 넣은 반환값을 dailyItem이라는 변수에 대입.
        for (Map.Entry<Integer, ItemStack> dailyItems : dailyItem.entrySet()) { //dailyItem.entrySet()의 배열의 값을 dailyItems에 대입하고, 그 배열의 길이만큼 반복
            gui.setItem(dailyItems.getKey(), dailyItems.getValue()); //gui에 dailyItems라는 맵의 키(Slot)와 값(ItemStack)을 토대로 설정.
        }
    }
    public void openGui(Player player) {
        Inventory dailyRewardGui = Bukkit.createInventory(null, 54, ChatColor.GREEN + "출석체크 GUI");
        //ChatColor.GREEN + "출석체크 GUI" 를 타이틀로 하는 54칸(6줄)의 GUI를 dailyRewardGui 변수에 대입
        loadItems(dailyRewardGui, player); //loadItems 메소드의 매개변수에 dailyRewardGui와 player를 넣고 실행.
        player.openInventory(dailyRewardGui); //dailyRewardGui를 오픈.
    }
}
