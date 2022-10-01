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
    private final Set<ItemMeta> dailyItemMetaSet = new HashSet<>();
    private File file = null;
    private FileConfiguration rewardsFile = null;
    public final Inventory DailyRewardGui = Bukkit.createInventory(null, 54, ChatColor.GREEN + "출석체크 GUI");

    public void createRewardsYml() {
        this.file = new File(main.getDataFolder(), "rewards.yml");

        if (!file.exists()) {
            main.saveResource("rewards.yml", false);
        }
        this.rewardsFile = YamlConfiguration.loadConfiguration(file);
    }
    public FileConfiguration getConfig() {
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
                List<String> commandList = new ArrayList<>();
                for (String lores : getConfig().getStringList(path + "." + key + ".lore")){
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lores));
                }
                for (String commands : getConfig().getStringList(path + "." + key + ".commands")){
                    commandList.add(commands);
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
        this.dailyItem.putAll(getRewards("Rewards"));
        for (ItemStack itemStack : this.dailyItem.values()) {
            this.dailyItemMetaSet.add(itemStack.getItemMeta());
        }
        for (Map.Entry<Integer, ItemStack> dailyitems : this.dailyItem.entrySet()){
            this.DailyRewardGui.setItem(dailyitems.getKey(), dailyitems.getValue());
        }
    }
}