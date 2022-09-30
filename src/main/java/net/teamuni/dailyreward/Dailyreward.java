package net.teamuni.dailyreward;

import org.bukkit.ChatColor;
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
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        RewardManager rm = new RewardManager();
        Player player = (Player) sender;
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            player.openInventory(rm.DailyRewardGui);
        }
        if (cmd.getName().equals("dailyreward") && player.hasPermission("dailyreward.reload")) {
            if (args.length > 0) {
                if (args[0].equals("reload")) {
                    rm.reload();
                    rm.setGui();
                    player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " DailyReward 플러그인이 리로드되었습니다.");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " 알 수 없는 명령어 입니다.");
                return true;
            }
        }
        return true;
    }
}
