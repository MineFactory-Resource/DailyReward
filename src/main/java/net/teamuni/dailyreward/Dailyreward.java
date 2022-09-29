package net.teamuni.dailyreward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Dailyreward extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        RewardManager rm = new RewardManager();
        rm.createRewardsYml();
        rm.setGui();
    }

    @Override
    public void onDisable() {
        RewardManager rm = new RewardManager();
        rm.save();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            player.openInventory(RewardManager.DailyRewardGui);
        }
        return true;
    }
}
