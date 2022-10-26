package net.teamuni.dailyreward;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
    private final Map<Integer, ItemStack> dailyItem = new HashMap<>();
    private File file = null;
    private FileConfiguration rewardsFile = null;
    public final Inventory dailyRewardGui = Bukkit.createInventory(null, 54, ChatColor.GREEN + "출석체크 GUI");

    public void createRewardsYml() {
        this.file = new File(main.getDataFolder(), "rewards.yml");

        if (!file.exists()) {
            main.saveResource("rewards.yml", false);
        }
        this.rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getRewardsFile() {
        return this.rewardsFile;
    }

    public void reload() {
        this.rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    /*
    public void save() {
        try{
            this.rewardsFile.save(file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
     */

    @NotNull
    public Map<Integer, ItemStack> getRewards(UUID uuid) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = this.rewardsFile.getConfigurationSection("Rewards");
        Map<Integer, ItemStack> rewards = new HashMap<>();
        Set<String> rewardsKeys = section.getKeys(false);
        if (rewardsKeys.isEmpty()) {
            throw new IllegalArgumentException("rewards.yml 파일의 내용이 비어있습니다. rewards.yml파일을 확인해주세요.");
        }
        for (String key : rewardsKeys) {
            ConfigurationSection sectionSecond = section.getConfigurationSection(key);
            int keyDay = Integer.parseInt(key.replaceAll("\\D", ""));
            int slot = sectionSecond.getInt("slot");
            try {
                ItemStack rewardsItem = new ItemStack(Material.valueOf(sectionSecond.getString("item_type")));
                ItemMeta meta = rewardsItem.getItemMeta();
                String rewardsName = sectionSecond.getString("name");
                List<String> rewardLoreList = new ArrayList<>();
                for (String lores : sectionSecond.getStringList("lore")) {
                    if(lores.contains("%rewards_receipt_status%")) {
                        if (keyDay > playerFile.getInt("CumulativeDate")) {
                            String placeholderLore = lores.replace("%rewards_receipt_status%", "아직 해당 일차보상을 획득할 수 없습니다.");
                            rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                        } else {
                            List<String> rewardList = playerFile.getStringList("ReceivedRewards");
                            if (rewardList.contains(key)) {
                                String placeholderLore = lores.replace("%rewards_receipt_status%", "이미 해당 일차보상을 수령했습니다.");
                                rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                            } else {
                                String placeholderLore = lores.replace("%rewards_receipt_status%", "해당 일차보상을 수령할 수 있습니다.");
                                rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                            }
                        }
                    } else {
                        rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lores));
                    }
                }
                if (rewardsName != null) {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardsName));
                } else {
                    Bukkit.getLogger().info("rewards.yml 파일중 보상의 이름이 없습니다. rewards.yml을 확인해주세요.");
                }
                meta.setLore(rewardLoreList);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                rewardsItem.setItemMeta(meta);
                rewards.put(slot, rewardsItem);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return rewards;
    }

    public void setGui(UUID uuid) {
        this.dailyItem.putAll(getRewards(uuid));
        for (Map.Entry<Integer, ItemStack> dailyItems : this.dailyItem.entrySet()) {
            dailyRewardGui.setItem(dailyItems.getKey(), dailyItems.getValue());
        }
    }
}