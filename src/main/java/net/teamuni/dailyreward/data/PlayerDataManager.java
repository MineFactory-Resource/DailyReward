package net.teamuni.dailyreward.data;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataManager {
    private final DailyReward main; //생성자 초기화

    public PlayerDataManager(DailyReward instance) { //PlayerDataManager 생성자
        this.main = instance;
    }

    public void createFolder() { //Players 이름의 폴더가 없으면 폴더를 생성해주는 함수
        File folder = new File(main.getDataFolder(), "Players");
        if (!folder.exists()) { //Players 폴더가 없으면,
            try {
                folder.mkdir(); //Players 폴더 생성
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createPlayerFile(UUID uuid) { //uuid의 값을 제목으로 하는 YAML 파일을 생성해주는 함수
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        if (!file.exists()) { //uuid의 YAML 파일이 없으면,
            try {
                FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
                playerFile.createSection("CumulativeDate");
                playerFile.createSection("LastJoinDate");
                playerFile.createSection("ReceivedRewards"); //CumulativeDate, LastJoinDate, ReceivedRewards 라는 섹션을 생성하고
                playerFile.set("CumulativeDate", 0); //CumulativeDate 섹션의 값을 0으로 설정하고
                playerFile.save(file); //uuid의 YAML 파일에 저장한다.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getPlayerFileConfiguration(UUID uuid) { //uuid의 YAML을 불러오는 함수
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public int getPlayerCumulativeDate(UUID uuid) { //uuid의 CumulativeDate 섹션의 값을 불러오는 함수
        return getPlayerFileConfiguration(uuid).getInt("CumulativeDate");
    }

    public List<String> getPlayerReceivedRewardsList(UUID uuid) { //uuid의 ReceivedRewards 섹션의 리스트 값을 불러오는 함수
        return getPlayerFileConfiguration(uuid).getStringList("ReceivedRewards");
    }
    public void addPlayerCumulativeDate(UUID uuid) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); //formatDate라는 변수에 지금 날짜를 "년도-월-일" 형식으로 지정한 값을 대입함.
        if (!Objects.equals(playerFile.getString("LastJoinDate"), formatDate)) { //uuid의 LastJoinDate값이 formatDate 변수의 값이랑 다를시
            int cumulativeDate = getPlayerCumulativeDate(uuid);
            try {
                playerFile.set("LastJoinDate", formatDate); //uuid YAML의 LastJoinDate 섹션의 값을 formatDate 변수값으로 설정하고
                playerFile.set("CumulativeDate", cumulativeDate + 1); //uuid YAML의 CumulativeDate 섹션의 값에 1을 더함.
                playerFile.save(file); //uuid의 YAML 파일에 저장한다.
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void updatePlayerRewardList(UUID uuid, String key, List<String> rewardList) {
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        try {
            rewardList.add(key); //rewardList라는 리스트에 key라는 문자열을 추가함.
            playerFile.set("ReceivedRewards", rewardList); //uuid의 ReceivedRewards 값을 rewardList 리스트로 설정함.
            playerFile.save(file); //uuid의 YAML 파일에 저장한다.
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

