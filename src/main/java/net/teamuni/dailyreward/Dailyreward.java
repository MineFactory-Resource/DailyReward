package net.teamuni.dailyreward;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Dailyreward extends JavaPlugin implements Listener {
    private RewardManager rewardManager;


    @Override
    public void onEnable() {
        this.rewardManager = new RewardManager();
        createFolder();
        rewardManager.createRewardsYml();
        rewardManager.setGui();
        midnightCheck();
        getServer().getPluginManager().registerEvents(new Event(this), this);
    }

    public void midnightCheck() {
        LocalTime midnightTime = LocalTime.of(0, 0);
        new BukkitRunnable(){
            @Override
            public void run() {
                LocalDate curdate = LocalDate.now();
                LocalDate MonthlastDate = curdate.withDayOfMonth(curdate.lengthOfMonth());
                LocalTime curTime = LocalTime.now();
                if(curTime.equals(midnightTime)) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        File file = new File("plugins/Dailyreward/Players", player.getUniqueId() + ".yml");
                        FileConfiguration playerfile = YamlConfiguration.loadConfiguration(file);
                        try {
                            String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            playerfile.set("Rewards.LastJoinDate", formatDate);
                            LocalDate LastJoinDate = LocalDate.parse(playerfile.getString("Rewards.LastJoinDate"), DateTimeFormatter.ISO_DATE);
                            if (LastJoinDate.isAfter(MonthlastDate)) {
                                playerfile.set("Rewards.Days", playerfile.getInt("Rewards.Days") + 1);
                            } else {
                                playerfile.set("Rewards.Days",1);
                            }
                            playerfile.save(file);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L);
    }

    public Inventory getGui() {
        return rewardManager.dailyRewardGui;
    }
    public FileConfiguration getRewardsFileConfiguration(){
        return rewardManager.getRewardsFile();
    }

    @Override
    public void onDisable() {
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
