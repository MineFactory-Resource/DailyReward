package net.teamuni.dailyreward;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class RewardManager implements Listener {
    private final Dailyreward main = Dailyreward.getPlugin(Dailyreward.class);
    private File file = null;
    private FileConfiguration rewardsFile = null;

    public void createRewardsYml() {
        this.file = new File(main.getDataFolder(), "rewards.yml");
        if (!file.exists()) {
            main.saveResource("rewards.yml", false);
        }
    }

    public FileConfiguration getRewardsFile() {
        return this.rewardsFile;
    }

    public void reload() {
        this.rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    private FileConfiguration getPlayerFileConfiguration(UUID uuid) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public int getKeyDay(String key) {
        return Integer.parseInt(key.replaceAll("\\D", ""));
    }

    public int getPlayerCumulativeDate(UUID uuid) {
        return getPlayerFileConfiguration(uuid).getInt("CumulativeDate");
    }

    public List<String> getPlayerReceivedRewardsList(UUID uuid) {
        return getPlayerFileConfiguration(uuid).getStringList("ReceivedRewards");
    }

    private List<String> rewardLoreSet(ConfigurationSection section, String key, UUID uuid) {
        List<String> rewardLoreList = new ArrayList<>();
        for (String lore : section.getStringList("lore")) {
            if (lore.contains("%rewards_receipt_status%")) {
                if (getKeyDay(key) > getPlayerCumulativeDate(uuid)) {
                    String placeholderLore = lore.replace("%rewards_receipt_status%", "아직 해당 일차보상을 획득할 수 없습니다.");
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                } else {
                    String placeholderLore;
                    if (getPlayerReceivedRewardsList(uuid).contains(key)) {
                        placeholderLore = lore.replace("%rewards_receipt_status%", "이미 해당 일차보상을 수령했습니다.");
                    } else {
                        placeholderLore = lore.replace("%rewards_receipt_status%", "해당 일차보상을 수령할 수 있습니다.");
                    }
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                }
            } else {
                rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lore));
            }
        }
        return rewardLoreList;
    }

    private ItemStack rewardItemSet(ConfigurationSection section, String key, UUID uuid) {
        ItemStack rewardsItem = null;
        try {
            rewardsItem = new ItemStack(Material.valueOf(section.getString("item_type")));
            ItemMeta meta = rewardsItem.getItemMeta();
            String rewardsName = section.getString("name");
            List<String> rewardLoreList = rewardLoreSet(section, key, uuid);
            if (rewardsName != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardsName));
            } else {
                Bukkit.getLogger().info("rewards.yml 파일중 보상의 이름이 없습니다. rewards.yml을 확인해주세요.");
            }
            meta.setLore(rewardLoreList);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            rewardsItem.setItemMeta(meta);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return rewardsItem;
    }

    @NotNull
    public Map<Integer, ItemStack> getRewards(UUID uuid) {
        getPlayerFileConfiguration(uuid);
        ConfigurationSection section = this.rewardsFile.getConfigurationSection("Rewards");
        Map<Integer, ItemStack> rewards = new HashMap<>();
        Set<String> rewardsKeys = section.getKeys(false);
        if (rewardsKeys.isEmpty()) {
            throw new IllegalArgumentException("rewards.yml 파일의 내용이 비어있습니다. rewards.yml파일을 확인해주세요.");
        }
        for (String key : rewardsKeys) {
            ConfigurationSection sectionSecond = section.getConfigurationSection(key);
            int slot = sectionSecond.getInt("slot");
            ItemStack rewardsItem = rewardItemSet(sectionSecond, key, uuid);
            rewards.put(slot, rewardsItem);
        }
        return rewards;
    }

    private void loadItems(Inventory gui, Player player) {
        Map<Integer, ItemStack> dailyItem = new HashMap<>(getRewards(player.getUniqueId()));
        for (Map.Entry<Integer, ItemStack> dailyItems : dailyItem.entrySet()) {
            gui.setItem(dailyItems.getKey(), dailyItems.getValue());
        }
    }

    public void openGui(Player player) {
        Inventory dailyRewardGui = Bukkit.createInventory(null, 54, ChatColor.GREEN + "출석체크 GUI");
        loadItems(dailyRewardGui, player);
        player.openInventory(dailyRewardGui);
    }
}

