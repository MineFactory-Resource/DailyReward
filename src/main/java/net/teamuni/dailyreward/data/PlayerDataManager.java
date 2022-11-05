package net.teamuni.dailyreward.data;

import net.teamuni.dailyreward.Dailyreward;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;
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
}

