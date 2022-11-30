package com.jannis.levelisborder.listener;

import com.jannis.levelisborder.LevelIsBorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEvent(PlayerMoveEvent event) {
        LevelIsBorder levelIsBorder = (LevelIsBorder) Bukkit.getPluginManager().getPlugin(LevelIsBorder.PLUGIN_NAME);
        boolean reloadBorder = levelIsBorder.isReloadBorder();
        boolean reloadInProgress = levelIsBorder.isReloadInProgress();
        Player latestPlayer = levelIsBorder.getLatestPlayer();
        if (reloadBorder && latestPlayer.getUniqueId() == event.getPlayer().getUniqueId()) {
            latestPlayer.setLevel(latestPlayer.getLevel() - 1);
            levelIsBorder.setReloadBorder(false);
            if (reloadInProgress) {
                levelIsBorder.setReloadInProgress(false);
            }
        }
    }
}
