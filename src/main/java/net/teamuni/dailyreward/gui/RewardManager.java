package net.teamuni.dailyreward.gui;

import net.teamuni.dailyreward.Dailyreward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class RewardManager implements Listener {
    private final Dailyreward main;
    private File file;
    private FileConfiguration rewardsFile = null;

    public RewardManager(Dailyreward instance) {
        this.main = instance;
    }

    public void createRewardsYml() {
        this.file = new File(main.getDataFolder(), "rewards.yml");
        if (!file.exists()) {
            main.saveResource("rewards.yml", false);
        }
    }

    public void reloadRewardsYml() {
        this.rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    public ConfigurationSection loadConfigurationSection() {
        return rewardsFile.getConfigurationSection("Rewards");
    }

    public String getDayBySlot(int slot) {
        ConfigurationSection section = this.rewardsFile.getConfigurationSection("Rewards");
        if (section == null) return null;
        return section.getKeys(false)
                .stream()
                .filter(key -> section.getInt(key + ".slot") == slot)
                .findFirst()
                .orElse(null);
    }

    public ConfigurationSection getSection(String key) {
        return loadConfigurationSection().getConfigurationSection(key);
    }

    public int getKeyDay(String key) {
        return Integer.parseInt(key.replaceAll("\\D", ""));
    }

    private List<String> rewardLore(ConfigurationSection section, String key, UUID uuid) {
        List<String> rewardLoreList = new ArrayList<>();
        for (String lore : section.getStringList("lore")) {
            if (lore.contains("%rewards_receipt_status%")) {
                if (getKeyDay(key) > main.getPlayerDataManager().getPlayerCumulativeDate(uuid)) {
                    String placeholderLore = lore.replace("%rewards_receipt_status%", "아직 해당 일차보상을 획득할 수 없습니다.");
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                } else {
                    String placeholderLore;
                    if (main.getPlayerDataManager().getPlayerReceivedRewardsList(uuid).contains(key)) {
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

    private ItemStack setRewardItem(ConfigurationSection section, String key, UUID uuid) {
        ItemStack rewardsItem = null;
        try {
            rewardsItem = new ItemStack(Material.valueOf(section.getString("item_type")));
            ItemMeta meta = rewardsItem.getItemMeta();
            String rewardsName = section.getString("name");
            List<String> rewardLoreList = rewardLore(section, key, uuid);
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
        main.getPlayerDataManager().getPlayerFileConfiguration(uuid);
        ConfigurationSection section = this.rewardsFile.getConfigurationSection("Rewards");
        Map<Integer, ItemStack> rewards = new HashMap<>();
        Set<String> rewardsKeys = section.getKeys(false);
        if (rewardsKeys.isEmpty()) {
            throw new IllegalArgumentException("rewards.yml 파일의 내용이 비어있습니다. rewards.yml파일을 확인해주세요.");
        }
        for (String key : rewardsKeys) {
            ConfigurationSection sectionSecond = section.getConfigurationSection(key);
            int slot = sectionSecond.getInt("slot");
            ItemStack rewardsItem = setRewardItem(sectionSecond, key, uuid);
            rewards.put(slot, rewardsItem);
        }
        return rewards;
    }
}
