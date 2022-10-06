package net.teamuni.dailyreward;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Event implements Listener {
    private final Inventory inventory;

    public Event(RewardManager rewardManager){ this.inventory = rewardManager.dailyRewardGui; }

    public ConfigurationSection loadConfiguration() {
        File file = new File("plugins/Dailyreward", "rewards.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return yaml.getConfigurationSection("Rewards");
    }
    @EventHandler
    public void joinEvent(PlayerJoinEvent e){
        File file = new File("plugins/Dailyreward/Players",e.getPlayer().getUniqueId()+".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }




    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Set<String> rewardsKeys = loadConfiguration().getKeys(false);
        if (!e.getInventory().equals(inventory)) return;
        if (e.getCurrentItem() == null) return;
        for (String key : rewardsKeys){
            ConfigurationSection section = loadConfiguration().getConfigurationSection(key);
            if (section.getString("slot") == null) return;
            if (e.getSlot() == section.getInt("slot")){
                List<String> commandList = section.getStringList("commands");
                try {
                    player.setOp(true);
                    for (String command : commandList) {
                        player.performCommand(command);
                    }
                } finally {
                    player.setOp(false);
                }
            }
        }
        e.setCancelled(true);
    }
    @EventHandler
    public void dragEvent(InventoryDragEvent e) {
        if (!e.getInventory().equals(inventory)) return;
        e.setCancelled(true);
    }
}
