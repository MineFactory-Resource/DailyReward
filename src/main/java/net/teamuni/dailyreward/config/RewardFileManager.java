package net.teamuni.dailyreward.config;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class RewardFileManager {
    private final DailyReward main;

    private File file;
    private FileConfiguration rewardsFile = null;


    public RewardFileManager(DailyReward instance) {
        this.main = instance;
    }

    public void createRewardsYml() {
        if (this.file == null) {
            this.file = new File(main.getDataFolder(), "rewards.yml");
        }
        if (!file.exists()) {
            main.saveResource("rewards.yml", false);
        }
        this.file = new File(main.getDataFolder(), "rewards.yml");
    }

    public void reloadRewardsYml() {
        if (this.file == null) {
            this.file = new File(main.getDataFolder(), "rewards.yml");
        }
        this.rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    public ConfigurationSection loadConfigurationSection() {
        return rewardsFile.getConfigurationSection("Rewards");
    }

    public FileConfiguration getRewardsFile() {
        return rewardsFile;
    }

    public String getDayBySlot(int slot) {
        ConfigurationSection section = main.getRewardFileManager().getRewardsFile().getConfigurationSection("Rewards");
        if (section == null) return null;
        return section.getKeys(false)
                .stream()
                .filter(key -> section.getInt(key + ".slot") == slot)
                .findFirst()
                .orElse(null);
    }

    public int getKeyDay(String key) {
        return Integer.parseInt(key.replaceAll("\\D", ""));
    }
}
