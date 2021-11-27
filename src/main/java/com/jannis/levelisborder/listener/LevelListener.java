package com.jannis.levelisborder.listener;

import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.jannis.levelisborder.LevelIsBorder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.util.List;

public class LevelListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onEvent(PlayerLevelChangeEvent event) {
        LevelIsBorder levelIsBorder = (LevelIsBorder) Bukkit.getPluginManager().getPlugin(LevelIsBorder.PLUGIN_NAME);
        double size = levelIsBorder.getSize();
        Position posCenter = levelIsBorder.getPosCenter();
        WorldBorderApi worldBorderApi = levelIsBorder.getWorldBorderApi();
        Player player = event.getPlayer();
        World world = player.getWorld();
        List<Player> players = world.getPlayers();
        double playerSize = (levelIsBorder.calculateSize(0, world));
        if (size >= 0) {
            playerSize = size + playerSize;
        }

        for (Player pl : players) {
            worldBorderApi.setBorder(pl, playerSize, posCenter);
        }
    }
}
