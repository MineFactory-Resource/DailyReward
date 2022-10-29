package net.teamuni.dailyreward;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class Dailyreward extends JavaPlugin implements Listener {
    private RewardManager rewardManager;
    public Dailyreward plugin;



    @Override
    public void onEnable() {
        this.rewardManager = new RewardManager();
        createFolder();
        createConfigFile();
        rewardManager.createRewardsYml();
        getServer().getPluginManager().registerEvents(new Event(this), this);
    }

    public Dailyreward getPlugin(){
        plugin = this;
        return plugin;
    }

    public FileConfiguration getRewardsFileConfiguration() {
        return rewardManager.getRewardsFile();
    }

    @Override
    public void onDisable() {
    }

    public void createConfigFile() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.saveResource("config.yml", false);
        }
    }

    public void createFolder() {
        File folder = new File(getDataFolder(), "Players");
        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        String openGuiSound = getConfig().getString("Open_Gui_Sound");
        String[] splitOpenGuiSound;
        if (openGuiSound != null && !openGuiSound.equals("")) {
            splitOpenGuiSound = openGuiSound.split("-");
        } else {
            player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " config.yml 에 Open_Gui_Sound가 비어있습니다. 관리자에게 연락해주세요.");
            return true;
        }
        Sound splitSound = Sound.valueOf(splitOpenGuiSound[0]);
        float splitVolume = Float.parseFloat(splitOpenGuiSound[1]);
        float splitPitch = Float.parseFloat(splitOpenGuiSound[2]);
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            rewardManager.openGui(player);
            player.playSound(player, splitSound, splitVolume, splitPitch);
        }
        if (cmd.getName().equals("dailyreward") && player.hasPermission("dailyreward.reload")) {
            if (args.length > 0) {
                if (args[0].equals("reload")) {
                    rewardManager.reload();
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
