package net.teamuni.dailyreward.command;

import net.teamuni.dailyreward.DailyReward;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DailyRewardCommand implements CommandExecutor {
    private final DailyReward main; //생성자 초기화

    public DailyRewardCommand(DailyReward instance) { //DailyRewardCommand 생성자
        this.main = instance;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender; //player 변수에 명령어를 입력한 sender를 대입함.
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) { //명령어의 이름이 /출석체크와 같으면서 dailyreward.opengui 펄미션을 플레이어가 갖고있다면,
            main.getDailyRewardGui().openGui(player); //출석체크 GUI를 오픈함.
        }
        if (cmd.getName().equals("dailyreward") && player.hasPermission("dailyreward.reload")) { //명령어의 이름이 /dailyreward와 같으면서 dailyreward.reload 펄미션을 플레이어가 갖고있고,
            if (args.length > 0) { //인자가 0개보다 많고,
                if (args[0].equals("reload")) { //첫번째 인자가 reload랑 같을때,
                    main.getRewardFileManager().reloadRewardsYml(); //RewardFileManager 클래스의 reloadRewardsYml 메소드를 실행하고
                    player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " DailyReward 플러그인이 리로드되었습니다."); //메세지를 출력함.
                } else { //아니라면
                    player.sendMessage(ChatColor.YELLOW + "[알림]" + ChatColor.WHITE + " 알 수 없는 명령어 입니다."); //메세지를 출력함
                }
            } else { //인자가 0개보다 적으면
                main.getDailyRewardGui().openGui(player); //출석체크 GUI를 오픈함.
            }
        }
        return true;
    }

    public void executeCommand(Player player, String key) {
        List<String> commandList = main.getRewardFileManager().loadConfigurationSection().getStringList(key + ".commands"); //key의 섹션의 commands 리스트 값을 commandList 변수에 대입.
        if (player.isOp()) { //플레이어가 관리자 권한을 갖고 있으면.
            for (String command : commandList) { //commandList의 배열의 값 하나하나를 command에 대입하고 배열의 길이만큼 반복.
                player.performCommand(command); // 플레이어에게 /(command의 배열의 값) 라는 명령어를 실행시킴.
            }
        } else { //관리자 권한을 갖고 있지 않다면
            player.setOp(true); //플레이어에게 관리자 권한을 참으로 설정하고, (관리자 권한이 지급됨)
            for (String command : commandList) { //commandList의 배열의 값 하나하나를 command에 대입하고 배열의 길이만큼 반복.
                player.performCommand(command); // 플레이어에게 /(command의 배열의 값) 라는 명령어를 실행시킴.
            }
            player.setOp(false); //플레이어에게 관리자 권한을 거짓으로 설정함. (관리자 권한이 회수됨)
        }
    }
}
