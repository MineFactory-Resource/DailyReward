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
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Event implements Listener {
    public Inventory inventory;
    public FileConfiguration rewardsFile;

    public Event(Dailyreward dailyreward) {
        this.inventory = dailyreward.getGui();
        this.rewardsFile = dailyreward.getRewardsFileConfiguration();
    }

    public ConfigurationSection loadConfiguration() {
        return rewardsFile.getConfigurationSection("Rewards");
    }

    public String getDayBySlot(int slot) {
        ConfigurationSection section = loadConfiguration();
        if (section == null) return null;
        return section.getKeys(false)
                .stream()
                .filter(key -> section.getInt(key + ".slot") == slot)
                .findFirst()
                .orElse(null);
    }

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

    @EventHandler
    public void joinEvent(PlayerJoinEvent e) {
        createPlayerYml(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) {
            e.setCancelled(true);
            return;
        }
        Player player = (Player) e.getWhoClicked();
        File file = new File("plugins/Dailyreward/Players", e.getWhoClicked().getUniqueId() + ".yml");
        if (!file.exists()) {
            createPlayerYml(e.getWhoClicked().getUniqueId());
            e.setCancelled(true);
            return;
        }
        FileConfiguration playerfile = YamlConfiguration.loadConfiguration(file);
        if (e.getCurrentItem() == null) return;
        String key = getDayBySlot(e.getSlot());
        if (key == null) return;
        ConfigurationSection section = loadConfiguration().getConfigurationSection(key);
        String rewardName = section.getString("name");
        List<String> commandList = section.getStringList("commands");
        if (Objects.equals(playerfile.getString("Rewards.receivedRewards." + key), "received")) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 이미 수령하셨습니다!");
            e.setCancelled(true);
            player.closeInventory();
            return;
        }
        try {
            player.setOp(true);
            for (String command : commandList) {
                player.performCommand(command);
            }
        } finally {
            player.setOp(false);
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 수령했습니다!");
            try {
                playerfile.createSection("Rewards.receivedRewards." + key);
                playerfile.set("Rewards.receivedRewards." + key, "received");
                playerfile.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            player.closeInventory();
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void dragEvent(InventoryDragEvent e) {
        if (!e.getInventory().equals(inventory)) return;
        e.setCancelled(true);
    }
}
