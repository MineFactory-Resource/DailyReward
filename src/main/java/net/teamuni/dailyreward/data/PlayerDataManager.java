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
    private final DailyReward main;

    public PlayerDataManager(DailyReward instance) {
        this.main = instance;
    }

    public void createFolder() { //Players 이름의 폴더가 없으면 폴더를 생성해주는 메소드
        File folder = new File(main.getDataFolder(), "Players");
        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createPlayerFile(UUID uuid) { //플레이어의 데이터가 담긴 YAML 파일을 생성해주는 메소드.
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        if (!file.exists()) {
            try {
                FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
                playerFile.createSection("CumulativeDate");
                playerFile.createSection("LastJoinDate");
                playerFile.createSection("ReceivedRewards");
                playerFile.set("CumulativeDate", 0);
                playerFile.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getPlayerFileConfiguration(UUID uuid) {  //플레이어의 데이터가 담긴 YAML 파일을 불러와주는 메소드.
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public int getPlayerCumulativeDate(UUID uuid) { //플레이어의 데이터가 담긴 YAML 파일의 CumulativeDate 부분의 값을 불러와주는 함수입니다.
        return getPlayerFileConfiguration(uuid).getInt("CumulativeDate");
    }

    public List<String> getPlayerReceivedRewardsList(UUID uuid) { //플레이어의 데이터가 담긴 YAML 파일의 ReceivedRewards 부분의 값들을 불러와주는 메소드.
        return getPlayerFileConfiguration(uuid).getStringList("ReceivedRewards");
    }

    public void addPlayerCumulativeDate(UUID uuid) { //플레이어의 데이터가 담긴 YAML 파일의 CumulativeDate 부분의 값을 추가해주는 메소드.
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!Objects.equals(playerFile.getString("LastJoinDate"), formatDate)) {
            int cumulativeDate = getPlayerCumulativeDate(uuid);
            try {
                playerFile.set("LastJoinDate", formatDate);
                playerFile.set("CumulativeDate", cumulativeDate + 1);
                playerFile.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void updatePlayerRewardList(UUID uuid, String key, List<String> rewardList) { //플레이어의 데이터가 담긴 YAML 파일의 RewardList 부분을 업데이트 해주는 메소드.
        File file = new File("plugins/Dailyreward/Players", uuid + ".yml");
        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
        try {
            rewardList.add(key);
            playerFile.set("ReceivedRewards", rewardList);
            playerFile.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

