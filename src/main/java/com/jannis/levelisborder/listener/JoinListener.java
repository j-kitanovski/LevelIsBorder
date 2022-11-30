package com.jannis.levelisborder.listener;

import com.jannis.levelisborder.LevelIsBorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setLevel(player.getLevel() + 1);
        LevelIsBorder levelIsBorder = (LevelIsBorder) Bukkit.getPluginManager().getPlugin(LevelIsBorder.PLUGIN_NAME);
        levelIsBorder.setLatestPlayer(player);
        levelIsBorder.setReloadBorder(true);
    }
}
