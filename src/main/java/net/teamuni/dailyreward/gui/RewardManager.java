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
    private final DailyReward main; //생성자 초기화

    public RewardManager(DailyReward instance) {
        this.main = instance;
    } //RewardManager 생성자

    private List<String> rewardLore(ConfigurationSection section, String key, UUID uuid) {
        List<String> rewardLoreList = new ArrayList<>(); //rewardLoreList 라는 변수에 String 타입으로 하는 ArrayList를 선언
        for (String lore : section.getStringList("lore")) { //section의 lore 섹션의 배열의 값을 lore에 대입후, 배열의 길이만큼 반복
            if (lore.contains("%rewards_receipt_status%")) { //lore에 "%rewards_receipt_status%" 값이 포함되어 있고,
                if (main.getRewardFileManager().getKeyDay(key) > main.getPlayerDataManager().getPlayerCumulativeDate(uuid)) {
                    //RewardFileManager 클래스에 있는 getKeyDay 메소드 매개변수에 key값을 넣은 반환값이, PlayerDataManager 클래스에 있는 PlayerCumulativeDate 메소드 매개변수에 uuid값을 넣은 반환값보다 크다면,
                    //즉, key의 일차가 플레이어의 누적일보다 크다면,
                    String placeholderLore = lore.
                            replace("%rewards_receipt_status%", "아직 해당 일차보상을 획득할 수 없습니다.");
                    //"%rewards_receipt_status%" 값을 "아직 해당 일차보상을 획득할 수 없습니다."로 변환후 placeholderLore 변수에 대입.
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                    //rewardLoreList에 placeholderLore 변수를 대입.
                } else { //key의 일차가 플레이어의 누적일보다 작다면,
                    String placeholderLore;
                    if (main.getPlayerDataManager().getPlayerReceivedRewardsList(uuid).contains(key)) {
                        //PlayerReceivedRewardsList 메소드 매개변수에 uuid값을 넣은 반환 리스트에 key값이 포함되어 있다면
                        placeholderLore = lore.
                                replace("%rewards_receipt_status%", "이미 해당 일차보상을 수령했습니다.");
                        //"%rewards_receipt_status%" 값을 "이미 해당 일차보상을 수령했습니다."로 변환후 placeholderLore 변수에 대입.
                    } else { //포함되어 있지 않다면,
                        placeholderLore = lore.
                                replace("%rewards_receipt_status%", "해당 일차보상을 수령할 수 있습니다.");
                        //"%rewards_receipt_status%" 값을 "해당 일차보상을 수령할 수 있습니다."로 변환후 placeholderLore 변수에 대입.
                    }
                    rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', placeholderLore));
                    //rewardLoreList에 placeholderLore 변수를 대입.
                }
            } else { //lore에 "%rewards_receipt_status%" 값이 포함되어 있지 않다면
                rewardLoreList.add(ChatColor.translateAlternateColorCodes('&', lore));
                //rewardLoreList에 lore 변수를 대입.
            }
        }
        return rewardLoreList; //rewardLoreList라는 리스트를 반환.
    }

    private ItemStack setRewardItem(ConfigurationSection section, String key, UUID uuid) {
        ItemStack rewardsItem = new ItemStack(Material.valueOf(section.getString("item_type"))); //rewardsItem 변수에 section이라는 섹션의 item_type 값을 ItemStack 형식으로 변환해 대입.
        ItemMeta meta = rewardsItem.getItemMeta(); //rewardsItem의 ItemMeta를 meta라는 변수에 대입함.
        String rewardsName = section.getString("name"); //rewardsName의 변수에 section이란느 섹션의 name값을 대입
        if (rewardsName != null) { //rewardsName이 null이 아니라면,
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardsName)); //meta에 표시되는 이름을 rewardsName로 설정
        } else { //null이라면,
            Bukkit.getLogger().info("rewards.yml 파일중 보상의 이름이 없습니다. rewards.yml을 확인해주세요."); //버킷에 메세지 출력
        }
        List<String> rewardLoreList = rewardLore(section, key, uuid); //rewardLore 메소드 매개변수에 section, key, uuid를 넣어 반환한 값을 rewardLoreList에 대입.
        meta.setLore(rewardLoreList); //meta의 로어(설명)을 rewardLoreList로 설정
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); //meta에 HIDE_ATTRIBUTES(아이템의 속성을 숨겨주는 플래그)라는 플래그를 추가함.
        rewardsItem.setItemMeta(meta); //rewardsItem의 ItemMeta를 meta로 설정함.
        return rewardsItem; //rewardsItem라는 ItemStack을 반환.
    }

    @NotNull
    public Map<Integer, ItemStack> getRewards(UUID uuid) {
        main.getPlayerDataManager().getPlayerFileConfiguration(uuid); //PlayerDataManager 클래스에 getPlayerFileConfiguration을 통해 플레이어의 uuid YAML파일을 불러옴.
        ConfigurationSection section = main.getRewardFileManager().loadConfigurationSection(); //section이라는 변수에 RewardFileManager 클래스에 loadConfigurationSection 메소드를 통해 읽어온 섹션값을 대입
        Map<Integer, ItemStack> rewards = new HashMap<>(); //rewards라는 변수에 정수형을 Key로 하고 ItemStack을 Value로 하는 HashMap을 선언
        Set<String> rewardsKeys = section.getKeys(false); //section에서 얻은 키들을 rewardsKeys에 대입함.
        if (rewardsKeys.isEmpty()) { //rewardsKeys의 값이 비어있을 때,
            throw new IllegalArgumentException("rewards.yml 파일의 내용이 비어있습니다. rewards.yml파일을 확인해주세요."); //오류를 출력함.
        }
        for (String key : rewardsKeys) { //rewardsKeys의 배열의 값을 key에 대입하고, rewardsKeys의 배열의 길이만큼 반복.
            ConfigurationSection sectionSecond = section.getConfigurationSection(key); //sectionSecond 변수에 key 섹션의 하위 섹션들을 대입함.
            int slot = sectionSecond.getInt("slot"); //slot라는 정수형 변수에 key 섹션의 하위 섹션인 slot의 값을 대입함.
            ItemStack rewardsItem = setRewardItem(sectionSecond, key, uuid); //rewardsItem 변수에 setRewardItem 메소드 매개변수에 sectionSecond, key, uuid를 넣어 반환한 값을 대입.
            rewards.put(slot, rewardsItem); //rewards라는 맵에 slot의 값을 key, rewardsItem을 value로 하는 값을 대입함
        }
        return rewards; //rewards라는 맵을 반환.
    }
}
