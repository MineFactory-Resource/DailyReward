package net.teamuni.dailyreward;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.List;

public class ClickEvent implements Listener {
    private final Dailyreward dailyreward = Dailyreward.getPlugin(Dailyreward.class);

    public ClickEvent(Dailyreward dailyreward) {
    }

    public ConfigurationSection loadConfiguration() {
        return dailyreward.rewardsYmlLoad().getConfigurationSection("Rewards");
    }

    public String getDayBySlot(int slot) {
        ConfigurationSection section = loadConfiguration();
        if (section == null) return null;
        return section.getKeys(false)
                .stream()
                .filter(key -> section.getInt(key + ".slot") == slot)
                .findFirst()
                .orElse(null);
    }


    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!e.getInventory().equals(dailyreward.getGui())) return;
        if (e.getCurrentItem() == null) return;
        if (getDayBySlot(e.getSlot()) == null) return;
        String key = getDayBySlot(e.getSlot());
        ConfigurationSection section = loadConfiguration().getConfigurationSection(key);
        List<String> commandList = section.getStringList("commands");
        try {
            player.setOp(true);
            for (String command : commandList) {
                player.performCommand(command);
            }
        } finally {
            player.setOp(false);
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void dragEvent(InventoryDragEvent e) {
        if (!e.getInventory().equals(dailyreward.getGui())) return;
        e.setCancelled(true);
    }
}
