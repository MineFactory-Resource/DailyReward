package net.teamuni.dailyreward;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class PlayerDataManager {
    public void createPlayerYml(UUID Uuid) {
        File file = new File("plugins/Dailyreward/Players", Uuid + ".yml");
        FileConfiguration playerfile = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                playerfile.createSection("Rewards");
                playerfile.createSection("Rewards.receivedRewards");
                playerfile.save(file);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
