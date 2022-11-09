package net.teamuni.dailyreward.event;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinEvent implements Listener {
    private final DailyReward main; //생성자 초기화

    public JoinEvent(DailyReward instance) {
        this.main = instance;
    } //JoinEvent 생성자

    @EventHandler
    public void joinEvent(PlayerJoinEvent event) { //플레이어 접속 이벤트가 발생했을 때,
        UUID uuid = event.getPlayer().getUniqueId(); //uuid 변수에 접속한 플레이어의 UUID를 대입함.
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> { //비동기 함수
            main.getPlayerDataManager().createPlayerFile(uuid); //PlayerDataManager 클래스의
            main.getPlayerDataManager().addPlayerCumulativeDate(uuid); //createPlayerFile, addPlayerCumulativeDate 메소드 매개변수에 uuid를 넣고 실행.
        });
    }
}
