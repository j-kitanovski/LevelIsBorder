package com.jannis.levelisborder.listener;

import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.jannis.levelisborder.LevelIsBorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class LevelListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onEvent(PlayerLevelChangeEvent event) {
        LevelIsBorder levelIsBorder = (LevelIsBorder) Bukkit.getPluginManager().getPlugin(LevelIsBorder.PLUGIN_NAME);
        double size = levelIsBorder.getSize();
        Position posCenter = levelIsBorder.getPosCenter();
        WorldBorderApi worldBorderApi = levelIsBorder.getWorldBorderApi();
        double playerSize = (levelIsBorder.calculateSize(0));
        if (size >= 0) {
            playerSize = size + playerSize;
        }

        for (Player pl : Bukkit.getOnlinePlayers()) {
            worldBorderApi.setBorder(pl, playerSize, posCenter);
        }
    }
}
