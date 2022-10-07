package net.teamuni.dailyreward;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Event implements Listener {
    private final Inventory inventory;

    public Event(RewardManager rewardManager){ this.inventory = rewardManager.dailyRewardGui; }

    public ConfigurationSection loadConfiguration() {
        File file = new File("plugins/Dailyreward", "rewards.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return yaml.getConfigurationSection("Rewards");
    }
    public void createPlayerYml(UUID Uuid){
        File file = new File("plugins/Dailyreward/Players",Uuid+".yml");
        FileConfiguration playerfile = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                playerfile.createSection("Rewards");
                playerfile.createSection("Rewards.receivedRewards");
                playerfile.save(file);
            } catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }
    @EventHandler
    public void joinEvent(PlayerJoinEvent e){
        createPlayerYml(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void clickEvent(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        File file = new File("plugins/Dailyreward/Players", e.getWhoClicked().getUniqueId()+".yml");
        if (!file.exists()){
            createPlayerYml(e.getWhoClicked().getUniqueId());
            e.setCancelled(true);
            return;
        }
        FileConfiguration playerfile = YamlConfiguration.loadConfiguration(file);
        Set<String> rewardsKeys = loadConfiguration().getKeys(false);
        if (!e.getInventory().equals(inventory)) return;
        if (e.getCurrentItem() == null) return;
        for (String key : rewardsKeys){
            ConfigurationSection section = loadConfiguration().getConfigurationSection(key);
            if (section.getString("slot") == null) return;
            if (Objects.equals(playerfile.getString("Rewards.receivedRewards." + key), "received")){
                player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " 해당 보상을 이미 수령하셨습니다!");
                e.setCancelled(true);
                player.closeInventory();
                return;
            }
            if (e.getSlot() == section.getInt("slot")){
                List<String> commandList = section.getStringList("commands");
                try {
                    player.setOp(true);
                    for (String command : commandList) {
                        player.performCommand(command);
                    }
                } finally {
                    player.setOp(false);
                    try {
                        playerfile.createSection("Rewards.receivedRewards."+key);
                        playerfile.set("Rewards.receivedRewards." + key, "received");
                        playerfile.save(file);
                    } catch (IOException exception){
                        exception.printStackTrace();
                    }
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
