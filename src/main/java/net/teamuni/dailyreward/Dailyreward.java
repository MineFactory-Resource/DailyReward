package net.teamuni.dailyreward;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Dailyreward extends JavaPlugin implements Listener {


    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            Inventory DailyRewardGui = Bukkit.createInventory(player, 54, ChatColor.GREEN + "출석체크 GUI");
            ItemStack day1 = new ItemStack(Material.CHEST_MINECART);
            ItemStack[] DailyRewardItems = {day1};
            DailyRewardGui.setContents(DailyRewardItems);
            player.openInventory(DailyRewardGui);
        }
        return true;
    }
}
