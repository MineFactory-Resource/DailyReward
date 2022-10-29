package net.teamuni.dailyreward;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Event implements Listener {
    public FileConfiguration rewardsFile;
    public Dailyreward plugin;
    public String receivedRewardSound;
    public String notEnoughDaySound;
    public String alreadyReceivedRewardSound;
    public String notExistPlayerFileSound;

    public Event(Dailyreward dailyreward) {
        this.rewardsFile = dailyreward.getRewardsFileConfiguration();
        this.plugin = dailyreward.getPlugin();
        this.receivedRewardSound = dailyreward.getConfig().getString("Receive_Reward_Sound");
        this.notEnoughDaySound = dailyreward.getConfig().getString("Not_Enough_Day_Sound");
        this.alreadyReceivedRewardSound = dailyreward.getConfig().getString("Already_Received_Reward_Sound");
        this.notExistPlayerFileSound = dailyreward.getConfig().getString("Not_Exist_Player_File_Sound");
    }

    public ConfigurationSection loadConfigurationSection() {
        return rewardsFile.getConfigurationSection("Rewards");
    }

    public String getDayBySlot(int slot) {
        ConfigurationSection section = loadConfigurationSection();
        if (section == null) return null;
        return section.getKeys(false)
                .stream()
                .filter(key -> section.getInt(key + ".slot") == slot)
                .findFirst()
                .orElse(null);
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!file.exists()) {
                try {
                    FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
                    playerFile.createSection("CumulativeDate");
                    playerFile.createSection("LastJoinDate");
                    playerFile.createSection("ReceivedRewards");
                    playerFile.set("CumulativeDate", 0);
                    playerFile.save(file);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
            if (!Objects.equals(playerFile.getString("LastJoinDate"), formatDate)) {
                int cumulativeDate = playerFile.getInt("CumulativeDate");
                try {
                    playerFile.set("LastJoinDate", formatDate);
                    playerFile.set("CumulativeDate", cumulativeDate + 1);
                    playerFile.save(file);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }


    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        String key = getDayBySlot(event.getSlot());
        if (key == null) return;
        Player player = (Player) event.getWhoClicked();
        File file = new File("plugins/Dailyreward/Players", player.getUniqueId() + ".yml");
        if (!file.exists()) {
            String[] splitNotExistPlayerFileSound = notExistPlayerFileSound.split("-");
            Sound splitSound = Sound.valueOf(splitNotExistPlayerFileSound[0]);
            float splitVolume = Float.parseFloat(splitNotExistPlayerFileSound[1]);
            float splitPitch = Float.parseFloat(splitNotExistPlayerFileSound[2]);
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.WHITE + "플레이어의 데이터파일이 존재하지 않습니다! 서버에 나갔다가 다시 접속해주세요!");
            player.playSound(player, splitSound, splitVolume,splitPitch);
            player.closeInventory();
            return;
        }
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        int keyDay = Integer.parseInt(key.replaceAll("\\D", ""));
        if (keyDay > playerFile.getInt("CumulativeDate")) {
            String[] splitNotEnoughDaySound = notEnoughDaySound.split("-");
            Sound splitSound = Sound.valueOf(splitNotEnoughDaySound[0]);
            float splitVolume = Float.parseFloat(splitNotEnoughDaySound[1]);
            float splitPitch = Float.parseFloat(splitNotEnoughDaySound[2]);
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.WHITE + "아직 해당 일차의 보상을 수령할 수 없습니다!");
            player.playSound(player, splitSound, splitVolume, splitPitch);
            player.closeInventory();
            return;
        }
        List<String> rewardList = playerFile.getStringList("ReceivedRewards");
        ConfigurationSection section = loadConfigurationSection().getConfigurationSection(key);
        if (section == null) return;
        String rewardName = section.getString("name");
        List<String> commandList = section.getStringList("commands");
        if (rewardList.contains(key)) {
            String[] splitAlreadyReceivedRewardSound = alreadyReceivedRewardSound.split("-");
            Sound splitSound = Sound.valueOf(splitAlreadyReceivedRewardSound[0]);
            float splitVolume = Float.parseFloat(splitAlreadyReceivedRewardSound[1]);
            float splitPitch = Float.parseFloat(splitAlreadyReceivedRewardSound[2]);
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 이미 수령하셨습니다!");
            player.playSound(player, splitSound, splitVolume, splitPitch);
            player.closeInventory();
            return;
        }
        try {
            player.setOp(true);
            for (String command : commandList) {
                player.performCommand(command);
            }
        } finally {
            player.setOp(false);
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 수령했습니다!");
            String[] splitReceivedRewardSound = receivedRewardSound.split("-");
            Sound splitSound = Sound.valueOf(splitReceivedRewardSound[0]);
            float splitVolume = Float.parseFloat(splitReceivedRewardSound[1]);
            float splitPitch = Float.parseFloat(splitReceivedRewardSound[2]);
            player.playSound(player, splitSound, splitVolume, splitPitch);
            try {
                rewardList.add(key);
                playerFile.set("ReceivedRewards", rewardList);
                playerFile.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            player.closeInventory();
        }
    }

    @EventHandler
    public void dragEvent(InventoryDragEvent e) {
        if (e.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) {
            e.setCancelled(true);
        }
    }
}
