package com.spigot.jannis.levelisborder;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class LevelIsBorder extends JavaPlugin {
    private WorldBorderApi worldBorderApi;

    private Player latestplayer;
    private boolean playerjoined;
    public class JoinListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerJoinEvent event){
            Player player = event.getPlayer();
            player.setLevel(player.getLevel() + 1);
            latestplayer = player;
            playerjoined = true;
        }
    }

    public class LevelListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onEvent(PlayerLevelChangeEvent event) {
            Player player = event.getPlayer();
            World world = player.getWorld();
            WorldBorder border = world.getWorldBorder();
            List<Player> players = world.getPlayers();

            int playerlevel = player.getLevel();
            int all_levels = 0;

            border.setCenter(0.0, 0.0);
            for (Player pl : players) {
                all_levels = all_levels + pl.getLevel();
            }
            for (Player pl : players) {
                worldBorderApi.setBorder(pl, (all_levels * 1.8) + 3);
            }

        }
    }

    public class MoveListener implements Listener {
        @EventHandler(priority = EventPriority.NORMAL)
        public void onEvent(PlayerMoveEvent event){
            if (playerjoined){
                latestplayer.setLevel(latestplayer.getLevel() - 1);
                playerjoined = false;
            }
        }
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        JoinListener joinListener = new JoinListener();
        pluginManager.registerEvents(joinListener, this);

        LevelListener levelListener = new LevelListener();
        pluginManager.registerEvents(levelListener, this);

        MoveListener moveListener = new MoveListener();
        pluginManager.registerEvents(moveListener, this);

        worldBorderApi = BorderAPI.getApi();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
