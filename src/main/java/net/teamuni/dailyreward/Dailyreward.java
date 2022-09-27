package net.teamuni.dailyreward;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Dailyreward extends JavaPlugin implements Listener {
    private final Map<Integer, ItemStack> dailyItem = new HashMap<>();
    private final Set<ItemMeta> dailyItemMetaSet = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        RewardManager.createRewardsYml();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            dailyItem.putAll(RewardManager.getRewards("Rewards"));
            for (ItemStack itemStack : dailyItem.values()) {
                this.dailyItemMetaSet.add(itemStack.getItemMeta());
            }
            Inventory DailyRewardGui = Bukkit.createInventory(player, 54, ChatColor.GREEN + "출석체크 GUI");
            for (Map.Entry<Integer, ItemStack> dailyitems : dailyItem.entrySet()){
                DailyRewardGui.setItem(dailyitems.getKey(), dailyitems.getValue());
            }
            player.openInventory(DailyRewardGui);
        }
        return true;
    }
}
