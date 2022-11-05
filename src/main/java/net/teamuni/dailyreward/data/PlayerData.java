package net.teamuni.dailyreward.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerData {
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

    public void addPlayerRewardList(UUID uuid, String key, List<String> rewardList) {
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
