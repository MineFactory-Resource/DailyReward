package net.teamuni.dailyreward;

import lombok.Getter;
import net.teamuni.dailyreward.command.DailyRewardCommand;
import net.teamuni.dailyreward.config.ConfigManager;
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
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private DailyRewardGui dailyRewardGui;
    private DailyRewardCommand dailyRewardCommand;

    @Override
    public void onEnable() {
        this.rewardManager = new RewardManager(this);
        this.rewardFileManager = new RewardFileManager(this);
        this.configManager = new ConfigManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.dailyRewardGui = new DailyRewardGui(this);
        this.dailyRewardCommand = new DailyRewardCommand(this);
        playerDataManager.createFolder();
        rewardFileManager.createRewardsYml();
        rewardFileManager.reloadRewardsYml();
        Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new ClickEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new DragEvent(), this);
        getCommand("출석체크").setExecutor(new DailyRewardCommand(this));
        getCommand("dailyreward").setExecutor(new DailyRewardCommand(this));
    }

    @Override
    public void onDisable() {
    }
}
