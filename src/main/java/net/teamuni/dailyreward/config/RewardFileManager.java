package net.teamuni.dailyreward.config;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class RewardFileManager {
    private final DailyReward main; //생성자 초기화

    private File file; //전역 변수 file
    private FileConfiguration rewardsFile = null; //전역 변수 rewardsFile 초기화


    public RewardFileManager(DailyReward instance) {
        this.main = instance;
    } //RewardFileManager 생성자

    public void createRewardsYml() {
        if (this.file == null) { //file의 내용이 비어있다면,
            this.file = new File(main.getDataFolder(), "rewards.yml"); //플러그인 데이터 파일이 있는 폴더를 반환함.
        }
        if (!file.exists()) { //file이 존재하지 않는다면,
            main.saveResource("rewards.yml", false); //리소스에 있는 rewards.yml 파일 양식을 생성함.
        }
        this.file = new File(main.getDataFolder(), "rewards.yml"); //플러그인 데이터 파일이 있는 폴더를 반환함.
    }

    public void reloadRewardsYml() {
        if (this.file == null) { //file의 내용이 비어있다면,
            this.file = new File(main.getDataFolder(), "rewards.yml"); //플러그인 데이터 파일이 있는 폴더를 반환함.
        }
        this.rewardsFile = YamlConfiguration.loadConfiguration(file); //file을 불러와 rewardsFile 전역 변수에 대입.
    }

    public ConfigurationSection loadConfigurationSection() {
        return rewardsFile.getConfigurationSection("Rewards"); //rewardsFile의 Rewards 섹션 구성을 불러옴.
    }

    public String getDayBySlot(int slot) {
        ConfigurationSection section = loadConfigurationSection(); //section 변수에 Rewards 섹션 구성을 대입함.
        if (section == null) return null; //section이 null이면 null을 반환.
        return section.getKeys(false) //section의 키들의 값을 얻고
                .stream() //키들을 순회함.
                .filter(key -> section.getInt(key + ".slot") == slot) //key의 섹션의 slot의 값이 slot 변수의 값이랑 같다면
                .findFirst() //그 key값을 리턴함.
                .orElse(null); //아니라면 null을 반환함.
    }

    public int getKeyDay(String key) {
        return Integer.parseInt(key.replaceAll("\\D", "")); //key값의 이름에서 영문자를 모두 지운 문자열을 정수형으로 변경해 반환함. (ex: Day31의 key는 31로 반환함.)
    }
}
