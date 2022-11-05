package net.teamuni.dailyreward.command;

import net.teamuni.dailyreward.Dailyreward;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DailyRewardCommand implements CommandExecutor {
    private final Dailyreward main;

    public DailyRewardCommand(Dailyreward instance) {
        this.main = instance;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            main.getDailyRewardGui().openGui(player);
        }
        if (cmd.getName().equals("dailyreward") && player.hasPermission("dailyreward.reload")) {
            if (args.length > 0) {
                if (args[0].equals("reload")) {
                    main.getRewardManager().reloadRewardsYml();
                    player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " DailyReward 플러그인이 리로드되었습니다.");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " 알 수 없는 명령어 입니다.");
                }
            } else {
                main.getDailyRewardGui().openGui(player);
            }
        }
        return true;
    }

    public void executeCommand(Player player, String key) {
        List<String> commandList = main.getRewardManager().getSection(key).getStringList("commands");
        if (player.isOp()) {
            for (String command : commandList) {
                player.performCommand(command);
            }
        } else {
            player.setOp(true);
            for (String command : commandList) {
                player.performCommand(command);
            }
            player.setOp(false);
        }
    }
}