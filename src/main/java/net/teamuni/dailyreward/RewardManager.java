package net.teamuni.dailyreward;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RewardManager implements Listener {
    private final Dailyreward main = Dailyreward.getPlugin(Dailyreward.class);
    private final Map<Integer, ItemStack> dailyItem = new HashMap<>();
    private static final Set<ItemMeta> dailyItemMetaSet = new HashSet<>();
    private static File file;
    private static FileConfiguration rewardsFile;

    public static final Inventory DailyRewardGui = Bukkit.createInventory(null, 54, ChatColor.GREEN + "출석체크 GUI");

    public void createRewardsYml() {
        file = new File(main.getDataFolder(), "rewards.yml");

        if (!file.exists()) {
            main.saveResource("rewards.yml", false);
        }
        rewardsFile = YamlConfiguration.loadConfiguration(file);
    }
    public static FileConfiguration getConfig() {
        return rewardsFile;
    }


    public void save() {
        try {
            rewardsFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void reload() {
        rewardsFile = YamlConfiguration.loadConfiguration(file);
    }

    @NotNull
    public Map<Integer, ItemStack> getRewards(String path){
        Map<Integer, ItemStack> rewards = new HashMap<>();
        Set<String> rewardsKeys = getConfig().getConfigurationSection(path).getKeys(false);
        if (rewardsKeys.isEmpty()) {
            throw new IllegalArgumentException("rewards.yml 에서 문제가 발생했습니다.");
        }
        for (String key : rewardsKeys) {
            int slot = getConfig().getInt(path + "." + key + ".slot");
            try {
                ItemStack rewardsItem = new ItemStack(Material.valueOf(getConfig().getString(path + "." + key +".item_type")));
                ItemMeta meta = rewardsItem.getItemMeta();
                String rewardsName = getConfig().getString(path + "." + key + ".name");
                List<String> rewardLoreList = new ArrayList<>();
                for (String lores : getConfig().getStringList(path + "." + key + ".lore")){
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lores));
                }
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardsName));
                meta.setLore(rewardLoreList);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                rewardsItem.setItemMeta(meta);
                rewards.put(slot, rewardsItem);
            } catch (NullPointerException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return rewards;
    }

    public void setGui(){
        dailyItem.putAll(getRewards("Rewards"));
        for (ItemStack itemStack : dailyItem.values()) {
            dailyItemMetaSet.add(itemStack.getItemMeta());
        }
        for (Map.Entry<Integer, ItemStack> dailyitems : dailyItem.entrySet()){
            DailyRewardGui.setItem(dailyitems.getKey(), dailyitems.getValue());
        }
    }
}