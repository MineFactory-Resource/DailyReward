package net.teamuni.dailyreward.gui;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RewardManager {
    private final DailyReward main;

    public RewardManager(DailyReward instance) {
        this.main = instance;
    }

    private List<String> rewardLore(ConfigurationSection section, String key, UUID uuid) { //%rewards_receipt_status% 가 포함된 로어를 플레이어의 상태에따라 변경시켜주는 메소드.
        List<String> rewardLoreList = new ArrayList<>();
        for (String lore : section.getStringList("lore")) {
            if (lore.contains("%rewards_receipt_status%")) {
                if (main.getRewardFileManager().getKeyDay(key) > main.getPlayerDataManager().getPlayerCumulativeDate(uuid)) {
                    String placeholderLore = lore.
                            replace("%rewards_receipt_status%", "아직 해당 일차보상을 획득할 수 없습니다.");
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                } else {
                    String placeholderLore;
                    if (main.getPlayerDataManager().getPlayerReceivedRewardsList(uuid).contains(key)) {
                        placeholderLore = lore.
                                replace("%rewards_receipt_status%", "이미 해당 일차보상을 수령했습니다.");
                    } else {
                        placeholderLore = lore.
                                replace("%rewards_receipt_status%", "해당 일차보상을 수령할 수 있습니다.");
                    }
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                }
            } else {
                rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lore));
            }
        }
        return rewardLoreList;
    }

    private ItemStack setRewardItem(ConfigurationSection section, String key, UUID uuid) { //설정한 rewards.yml 파일에 맞게 GUI 에 들어갈 아이템의 타입과 이름, 로어를 설정해주는 메소드.
        ItemStack rewardsItem = new ItemStack(Material.valueOf(section.getString("item_type")));
        ItemMeta meta = rewardsItem.getItemMeta();
        String rewardsName = section.getString("name");
        if (rewardsName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardsName));
        } else {
            Bukkit.getLogger().info("rewards.yml 파일중 보상의 이름이 없습니다. rewards.yml을 확인해주세요.");
        }
        List<String> rewardLoreList = rewardLore(section, key, uuid);
        meta.setLore(rewardLoreList);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        rewardsItem.setItemMeta(meta);
        return rewardsItem;
    }

    @NotNull
    public Map<Integer, ItemStack> getRewards(UUID uuid) { //rewards.yml 에 있는 보상들의 slot 값과, setRewardItem 메소드에서 설정한 아이템들을 불러와주는 메소드.
        main.getPlayerDataManager().getPlayerFileConfiguration(uuid);
        ConfigurationSection section = main.getRewardFileManager().loadConfigurationSection();
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
