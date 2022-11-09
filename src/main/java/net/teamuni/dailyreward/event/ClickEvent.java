package net.teamuni.dailyreward.event;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.UUID;

public class ClickEvent implements Listener {
    private final DailyReward main; //생성자 초기화

    public ClickEvent(DailyReward instance) {
        this.main = instance;
    } //ClickEvent 생성자

    @EventHandler
    public void clickEvent(InventoryClickEvent event) { //인벤토리의 클릭이벤트가 발생했을때,
        if (!event.getView().getTitle().equals(ChatColor.GREEN + "출석체크 GUI")) return; //인벤토리의 타이틀이 "ChatColor.GREEN + "출석체크 GUI"" 가 아니면, 클릭이벤트 함수를 끝냄.
        event.setCancelled(true); // 클릭이벤트를 취소함
        if (event.getCurrentItem() == null) return; //클릭을 한 슬롯에 아이템이 없을때, 클릭이벤트 함수를 끝냄.
        String key = main.getRewardFileManager().getDayBySlot(event.getSlot()); //key 변수에 RewardFileManager 클래스에 있는 getDayBySlot 메소드 매개변수에 클릭한 슬롯의 값을 넣어 반환된값을 대입함.
        Player player = (Player) event.getWhoClicked(); //player 변수에 인벤토리를 클릭한 사람을 대입함.
        UUID uuid = player.getUniqueId(); //uuid 변수에 player의 UUID를 대입함.
        if (main.getRewardFileManager().getKeyDay(key) > main.getPlayerDataManager().getPlayerCumulativeDate(uuid)) {
            //RewardFileManager 클래스에 있는 getKeyDay 메소드 매개변수에 key값을 넣은 반환값이, PlayerDataManager 클래스에 있는 PlayerCumulativeDate 메소드 매개변수에 uuid값을 넣은 반환값보다 크다면,
            //즉, key의 일차가 플레이어의 누적일보다 크다면,
            player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.WHITE + "아직 해당 일차의 보상을 수령할 수 없습니다!"); //메세지를 출력하고
            player.closeInventory(); //플레이어가 열고있던 GUI 닫음.
            return; //함수 종료
        }
        String rewardName = main.getRewardFileManager().loadConfigurationSection().getString(key + ".name"); // key 변수의 name 섹션값을 rewardName 변수에 대입.
        List<String> rewardList = main.getPlayerDataManager().getPlayerReceivedRewardsList(uuid); //PlayerReceivedRewardsList 메소드 매개변수에 uuid값을 넣은 반환 리스트를 rewardList에 대입
        if (rewardList.contains(key)) { //rewardList 리스트에 key 값이 포함되어 있으면,
            //즉, 플레이어가 받은 보상에 key라는 보상이 존재하면.
            player.sendMessage(ChatColor.YELLOW + "[알림] " +
                    ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 이미 수령하셨습니다!"); //메세지를 출력하고
            player.closeInventory(); //플레이어가 열고있던 GUI 닫음.
            return; //함수 종료
        }
        main.getDailyRewardCommand().executeCommand(player, key); //DailyRewardCommand 클래스의 executeCommand 메소드 매개변수에 player와 key를 넣어서 실행.
        main.getPlayerDataManager().updatePlayerRewardList(uuid, key, rewardList); //PlayerDataManager 클래스의 updatePlayerRewardList 메소드 매개변수에 uuid, key, rewardList를 넣어서 실행.
        player.sendMessage(ChatColor.YELLOW + "[알림] " + ChatColor.translateAlternateColorCodes('&', rewardName) + ChatColor.WHITE + " 을(를) 수령했습니다!"); //메세지를 출력하고
        player.closeInventory(); //플레이어가 열고있던 GUI 닫음.
    }
}
