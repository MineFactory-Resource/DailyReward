package net.teamuni.dailyreward.data;

import net.teamuni.dailyreward.Dailyreward;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataManager implements Listener {
    private final Dailyreward main;

    public PlayerDataManager(Dailyreward instance) {
        this.main = instance;
    }

    public void createFolder() {
        File folder = new File(main.getDataFolder(), "Players");
        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createPlayerFile(UUID uuid) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        if (!file.exists()) {
            try {
                FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
                playerFile.createSection("CumulativeDate");
                playerFile.createSection("LastJoinDate");
                playerFile.createSection("ReceivedRewards");
                playerFile.set("CumulativeDate", 0);
                playerFile.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getPlayerFileConfiguration(UUID uuid) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public int getPlayerCumulativeDate(UUID uuid) {
        return getPlayerFileConfiguration(uuid).getInt("CumulativeDate");
    }

    public List<String> getPlayerReceivedRewardsList(UUID uuid) {
        return getPlayerFileConfiguration(uuid).getStringList("ReceivedRewards");
    }
    public void addPlayerCumulativeDate(UUID uuid) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
    }

    public void updatePlayerRewardList(UUID uuid, String key, List<String> rewardList) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        try {
            rewardList.add(key);
            playerFile.set("ReceivedRewards", rewardList);
            playerFile.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

