package net.teamuni.dailyreward.config;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ConfigManager {

    private final DailyReward main;

    private File file;
    private FileConfiguration configFile;

    public ConfigManager(DailyReward instance) {
        this.main = instance;
    }

    public void createConfigFile() { //config.yml 파일을 생성해주는 메소드
        if (this.file == null) {
            this.file = new File(main.getDataFolder(), "config.yml");
        }
        if (!this.file.exists()) {
            main.saveResource("config.yml", false);
        }
        this.file = new File(main.getDataFolder(), "config.yml");
    }

    public void reloadConfigFile() { //config.yml 파일을 불러와주는 메소드
        if (this.file == null) {
            this.file = new File(main.getDataFolder(), "config.yml");
        }
        this.configFile = YamlConfiguration.loadConfiguration(file);
    }

    public ConfigurationSection loadConfigurationSection(String path) { //config.yml의 특정 경로(path)의 섹션을 불러와주는 메소드
        return this.configFile.getConfigurationSection(path);
    }

    public String getMessage(String path) { //config.yml의 안에있는 Messages 섹션의 메세지를 얻어주는 메소드
        String Message = loadConfigurationSection("Messages.").getString(path);
        if (Message == null) return null;
        return ChatColor.translateAlternateColorCodes('&', Message);
    }

    public void playSound(Player player, String path) { //config.yml 안에 있는 Sounds 섹션의 소리를 재생시켜주는 메소드
        String pathSound = loadConfigurationSection("Sounds").getString(path);
        if (pathSound == null) return;
        String[] splitSound = pathSound.split("-");
        Sound sound = Sound.valueOf(splitSound[0]);
        float volume = Float.parseFloat(splitSound[1]);
        float pitch = Float.parseFloat(splitSound[2]);
        player.playSound(player, sound, volume, pitch);
    }
}
