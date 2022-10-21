package net.teamuni.dailyreward;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class PlayerDataManager {
    public void createPlayerYml(UUID Uuid) {
        File file = new File("plugins/Dailyreward/Players", Uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                playerFile.createSection("CumulativeDate");
                playerFile.createSection("LastJoinDate");
                playerFile.createSection("ReceivedRewards");
                playerFile.set("CumulativeDate", 0);
                playerFile.save(file);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
