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

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        PlayerDataManager pdm = new PlayerDataManager();
        pdm.createPlayerYml(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        String key = getDayBySlot(event.getSlot());
        if (key == null) return;
        Player player = (Player) event.getWhoClicked();
        File file = new File("plugins/Dailyreward/Players", player.getUniqueId() + ".yml");
        if (!file.exists()) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.WHITE + " 플레이어의 데이터파일이 존재하지 않습니다! 서버에 나갔다가 다시 접속해주세요!");
            return;
        }
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        List<String> rewardList = playerFile.getStringList("ReceivedRewards");
        ConfigurationSection section = loadConfiguration().getConfigurationSection(key);
        String rewardName = section.getString("name");
        List<String> commandList = section.getStringList("commands");
        if (rewardList.contains(key)) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 이미 수령하셨습니다!");
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
                rewardList.add(key);
                playerFile.set("ReceivedRewards", rewardList);
                playerFile.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            player.closeInventory();
        }
    }

    @EventHandler
    public void dragEvent(InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
