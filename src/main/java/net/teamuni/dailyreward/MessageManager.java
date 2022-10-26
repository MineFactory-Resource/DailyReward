package net.teamuni.dailyreward;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;

public class MessageManager implements Listener {
    private final Dailyreward main = Dailyreward.getPlugin(Dailyreward.class);

    private File file = null;
    private FileConfiguration messageFile = null;

    public void createMessagesYml() {
        this.file = new File(main.getDataFolder(), "message.yml");

        if (!file.exists()) {
            main.saveResource("message.yml", false);
        }
        this.messageFile = YamlConfiguration.loadConfiguration(file);
    }

    public String getMessage(String path){
        return this.messageFile.getString(path);
    }
}
