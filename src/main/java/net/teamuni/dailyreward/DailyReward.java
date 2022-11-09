package net.teamuni.dailyreward;

import lombok.Getter;
import net.teamuni.dailyreward.command.DailyRewardCommand;
import net.teamuni.dailyreward.config.RewardFileManager;
import net.teamuni.dailyreward.data.PlayerDataManager;
import net.teamuni.dailyreward.event.ClickEvent;
import net.teamuni.dailyreward.event.DragEvent;
import net.teamuni.dailyreward.event.JoinEvent;
import net.teamuni.dailyreward.gui.DailyRewardGui;
import net.teamuni.dailyreward.gui.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter //lombok Getter 기능사용
public final class DailyReward extends JavaPlugin implements Listener {
    private RewardManager rewardManager;
    private RewardFileManager rewardFileManager;
    private PlayerDataManager playerDataManager;
    private DailyRewardGui dailyRewardGui;
    private DailyRewardCommand dailyRewardCommand;

    @Override
    public void onEnable() {
        this.rewardManager = new RewardManager(this); //매개변수를 갖는 RewardManager 생성자 호출
        this.rewardFileManager = new RewardFileManager(this); //매개변수를 갖는 RewardFileManager 생성자 호출
        this.playerDataManager = new PlayerDataManager(this); //매개변수를 갖는 PlayerDataManager 생성자 호출
        this.dailyRewardGui = new DailyRewardGui(this); //매개변수를 갖는 DailyRewardGui 생성자 호출
        this.dailyRewardCommand = new DailyRewardCommand(this); //매개변수를 갖는 DailyRewardCommand 생성자 호출
        playerDataManager.createFolder(); //PlayerDataManager 클래스에 있는 createFolder 함수 실행.
        rewardFileManager.createRewardsYml(); //RewardFileManager 클래스에 있는 createRewardsYml 함수 실행.
        rewardFileManager.reloadRewardsYml(); //RewardFileManager 클래스에 있는 reloadRewardsYml 함수 실행.
        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this); //JoinEvent 리스너 클래스의 모든 이벤트를 등록함.
        Bukkit.getPluginManager().registerEvents(new ClickEvent(this), this); //ClickEvent 리스너 클래스의 모든 이벤트를 등록함.
        Bukkit.getPluginManager().registerEvents(new DragEvent(), this); //DragEvent 리스너 클래스의 모든 이벤트를 등록함.
        getCommand("출석체크").setExecutor(new DailyRewardCommand(this)); //"출석체크" 라는 이름의 명령을 DailyRewardCommand 클래스에서 가져옴.
        getCommand("dailyreward").setExecutor(new DailyRewardCommand(this)); //"dailyreward" 라는 이름의 명령을 DailyRewardCommand 클래스에서 가져옴.
    }

    @Override
    public void onDisable() {
    }
}
