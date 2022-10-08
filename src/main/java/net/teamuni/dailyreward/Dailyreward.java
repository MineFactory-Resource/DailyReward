package net.teamuni.dailyreward;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class Dailyreward extends JavaPlugin implements Listener {
    private RewardManager rewardManager;


    @Override
    public void onEnable() {
        this.rewardManager = new RewardManager();
        getServer().getPluginManager().registerEvents(new Event(this), this);
        createFolder();
        rewardManager.createRewardsYml();
        rewardManager.setGui();
    }

    @Override
    public void onDisable() {
    }


    public YamlConfiguration rewardsYmlLoad(){
        File file = new File(this.getDataFolder(), "rewards.yml");
        return YamlConfiguration.loadConfiguration(file);
    }


    public Inventory getGui() {
        return rewardManager.dailyRewardGui;
    }

    public void createFolder(){
        File folder = new File(getDataFolder(), "Players");
        if (!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            player.openInventory(rewardManager.dailyRewardGui);
        }
        if (cmd.getName().equals("dailyreward") && player.hasPermission("dailyreward.reload")) {
            if (args.length > 0) {
                if (args[0].equals("reload")) {
                    rewardManager.reload();
                    rewardManager.setGui();
                    createFolder();
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
