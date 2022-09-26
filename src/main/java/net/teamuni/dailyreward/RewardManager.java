package net.teamuni.dailyreward;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RewardManager {
    private static final Dailyreward main = Dailyreward.getPlugin(Dailyreward.class);
    private static File file;
    private static FileConfiguration rewardsFile;

    public static void createRewardsYml() {
        file = new File(main.getDataFolder(), "rewards.yml");

        if (!file.exists()) {
            main.saveResource("messages.yml", false);
        }
        rewardsFile = YamlConfiguration.loadConfiguration(file);
    }
    public FileConfiguration getConfig() {
        return rewardsFile;
    }


    public static void save() {
        try {
            rewardsFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void reload() {
        rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    @NotNull
    public Map<Integer, ItemStack> getRewards(String path){
        Map<Integer, ItemStack> rewards = new HashMap<>();
        Set<String> rewardsKeys = this.getConfig().getConfigurationSection(path).getKeys(false);
        if (rewardsKeys.isEmpty()) {
            throw new IllegalArgumentException("rewards.yml 에서 문제가 발생했습니다.");
        }
        for (String key : rewardsKeys) {
            int slot = this.getConfig().getInt(path + "." + key + ".slot");
            try {
                ItemStack rewardsItem = new ItemStack(Material.valueOf(this.getConfig().getString(path + "." + key +".item_type")));
                ItemMeta meta = rewardsItem.getItemMeta();
                String rewardsName = this.getConfig().getString(path + "." + key + ".name");
                List<String> rewardLoreList = new ArrayList<>();
                for (String lores : this.getConfig().getStringList(path + "." + key + ".lore")){
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lores));
                }
                List<String> rewardCommandList = new ArrayList<>();
                for (String rewardscommands : this.getConfig().getStringList(path + "." + key + ".commands")){
                    rewardCommandList.add(rewardscommands);
                }
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardsName));
                meta.setLore(rewardLoreList);
                rewardsItem.setItemMeta(meta);
                rewards.put(slot, rewardsItem);
            } catch (NullPointerException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return rewards;
    }
}