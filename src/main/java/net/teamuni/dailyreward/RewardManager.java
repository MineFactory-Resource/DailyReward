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
import java.io.IOException;
import java.util.*;

public class RewardManager implements Listener {
    private final Dailyreward main = Dailyreward.getPlugin(Dailyreward.class);
    private final Map<Integer, ItemStack> dailyItem = new HashMap<>();
    private final Set<ItemMeta> dailyItemMetaSet = new HashSet<>();
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
        ConfigurationSection section = this.rewardsFile.getConfigurationSection(path);
        Set<String> rewardsKeys = section.getKeys(false);
        if (rewardsKeys.isEmpty()) {
            throw new IllegalArgumentException("rewards.yml 파일의 내용이 비어있습니다. rewards.yml파일을 확인해주세요.");
        }
        for (String key : rewardsKeys) {
            int slot = section.getInt(key + ".slot");
            try {
                ItemStack rewardsItem = new ItemStack(Material.valueOf(section.getString(key + ".item_type")));
                ItemMeta meta = rewardsItem.getItemMeta();
                String rewardsName = section.getString(key + ".name");
                List<String> rewardLoreList = new ArrayList<>();
                List<String> commandList = new ArrayList<>();
                for (String lores : section.getStringList(key + ".lore")){
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lores));
                }
                for (String commands : section.getStringList(key + ".commands")){
                    commandList.add(commands);
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

    public void setGui(){
        this.dailyItem.putAll(getRewards("Rewards"));
        for (ItemStack itemStack : this.dailyItem.values()) {
            this.dailyItemMetaSet.add(itemStack.getItemMeta());
        }
        for (Map.Entry<Integer, ItemStack> dailyitems : this.dailyItem.entrySet()){
            this.dailyRewardGui.setItem(dailyitems.getKey(), dailyitems.getValue());
        }
    }
}