package net.teamuni.dailyreward;

import org.bukkit.Bukkit;
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

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Event implements Listener {
    private final FileConfiguration rewardsFile;
    private final Dailyreward plugin;
    private final RewardManager rewardManager = new RewardManager();

    public Event(Dailyreward dailyreward) {
        this.rewardsFile = dailyreward.getRewardsFileConfiguration();
        this.plugin = dailyreward.getPlugin();
    }

    public ConfigurationSection loadConfigurationSection() {
        return rewardsFile.getConfigurationSection("Rewards");
    }

    private void createPlayerFile(UUID uuid) {
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

    private void addPlayerCumulativeDate(UUID uuid) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!Objects.equals(playerFile.getString("LastJoinDate"), formatDate)) {
            int cumulativeDate = playerFile.getInt("CumulativeDate");
            try {
                playerFile.set("LastJoinDate", formatDate);
                playerFile.set("CumulativeDate", cumulativeDate + 1);
                playerFile.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            createPlayerFile(uuid);
            addPlayerCumulativeDate(uuid);
        });
    }

    private String getDayBySlot(int slot) {
        ConfigurationSection section = loadConfigurationSection();
        if (section == null) return null;
        return section.getKeys(false)
                .stream()
                .filter(key -> section.getInt(key + ".slot") == slot)
                .findFirst()
                .orElse(null);
    }

    private void executeCommand(Player player, String key) {
        List<String> commandList = getSection(key).getStringList("commands");
        player.setOp(true);
        for (String command : commandList) {
            player.performCommand(command);
        }
        player.setOp(false);
    }

    private ConfigurationSection getSection(String path) {
        return loadConfigurationSection().getConfigurationSection(path);
    }

    private void addPlayerRewardList(UUID uuid, String key, List<String> rewardList) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        try {
            rewardList.add(key);
            playerFile.set("ReceivedRewards", rewardList);
            playerFile.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    @EventHandler
    public void clickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;
        String key = getDayBySlot(event.getSlot());
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        if (rewardManager.getKeyDay(key) > rewardManager.getPlayerCumulativeDate(uuid)) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.WHITE + " 아직 해당 일차의 보상을 수령할 수 없습니다!");
            return;
        }
        String rewardName = getSection(key).getString("name");
        List<String> rewardList = rewardManager.getPlayerReceivedRewardsList(uuid);
        if (rewardList.contains(key)) {
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 이미 수령하셨습니다!");
            player.closeInventory();
            return;
        }
        executeCommand(player, key);
        addPlayerRewardList(uuid, key, rewardList);
        player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 수령했습니다!");
        player.closeInventory();
    }

    @EventHandler
    public void dragEvent(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return;
        event.setCancelled(true);
    }
}
