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

    public void createRewardsYml() { //rewards.yml 파일을 생성해주는 메소드.
        if (this.file == null) {
            this.file = new File(main.getDataFolder(), "rewards.yml");
        }
        if (!file.exists()) {
            main.saveResource("rewards.yml", false);
        }
        this.file = new File(main.getDataFolder(), "rewards.yml");
    }

    public void reloadRewardsYml() { //rewards.yml 파일을 읽어와주는 메소드.
        if (this.file == null) {
            this.file = new File(main.getDataFolder(), "rewards.yml");
        }
        this.rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    public ConfigurationSection loadConfigurationSection() { //reward.yml 파일에서 Rewards 섹션의 하위섹션을 읽어와주는 함수입니다.
        return rewardsFile.getConfigurationSection("Rewards");
    }

    public String getDayBySlot(int slot) { //클릭한 GUI 아이템의 key 값을 얻는 메소드.
        ConfigurationSection section = loadConfigurationSection();
        if (section == null) return null;
        return section.getKeys(false)
                .stream()
                .filter(key -> section.getInt(key + ".slot") == slot)
                .findFirst()
                .orElse(null);
    }

    public int getKeyDay(String key) { //key 값의 일차를 얻는 메소드.
        return Integer.parseInt(key.replaceAll("\\D", ""));
    }
}
