package net.teamuni.dailyreward.event;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinEvent implements Listener {
    private final DailyReward main;

    public JoinEvent(DailyReward instance) {
        this.main = instance;
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            main.getPlayerDataManager().createPlayerFile(uuid);
            main.getPlayerDataManager().addPlayerCumulativeDate(uuid);
        });
    }
}
