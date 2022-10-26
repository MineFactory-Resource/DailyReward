package net.teamuni.dailyreward;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public final class Dailyreward extends JavaPlugin implements Listener {
    private RewardManager rewardManager;
    private MessageManager messageManager;
    public Dailyreward plugin;



    @Override
    public void onEnable() {
        this.rewardManager = new RewardManager();
        this.messageManager = new MessageManager();
        createFolder();
        rewardManager.createRewardsYml();
        messageManager.createMessagesYml();
        getServer().getPluginManager().registerEvents(new Event(this), this);
    }

    public Dailyreward getPlugin(){
        plugin = this;
        return plugin;
    }

    public Inventory getGui() {
        return rewardManager.dailyRewardGui;
    }

    public FileConfiguration getRewardsFileConfiguration() {
        return rewardManager.getRewardsFile();
    }

    @Override
    public void onDisable() {
    }

    public void createFolder() {
        File folder = new File(getDataFolder(), "Players");
        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equals("출석체크") && player.hasPermission("dailyreward.opengui")) {
            player.sendMessage(messageManager.getMessage("test"));
            rewardManager.setGui(player.getUniqueId());
            player.openInventory(rewardManager.dailyRewardGui);
        }
        if (cmd.getName().equals("dailyreward") && player.hasPermission("dailyreward.reload")) {
            if (args.length > 0) {
                if (args[0].equals("reload")) {
                    rewardManager.reload();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageManager.getMessage("Reload_message")));
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageManager.getMessage("Unknown_command")));
                return true;
            }
        }
        return true;
    }
}
