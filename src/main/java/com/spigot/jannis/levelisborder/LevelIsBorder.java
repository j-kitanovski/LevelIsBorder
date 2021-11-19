package com.spigot.jannis.levelisborder;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class LevelIsBorder extends JavaPlugin {
    private WorldBorderApi worldBorderApi;

    private Player latestplayer;
    private boolean reloadborder;
    public class JoinListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerJoinEvent event){
            Player player = event.getPlayer();
            player.setLevel(player.getLevel() + 1);
            latestplayer = player;
            reloadborder = true;
        }
    }

    public class DeathListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerDeathEvent event){
            Player player = event.getEntity();
            player.setLevel(player.getLevel() + 1);
            latestplayer = player;
            reloadborder = true;
        }
    }

    public class LevelListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onEvent(PlayerLevelChangeEvent event) {
            Player player = event.getPlayer();
            World world = player.getWorld();
            List<Player> players = world.getPlayers();

            int playerlevel = player.getLevel();
            int all_levels = 0;

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
            if (reloadborder && latestplayer.getUniqueId() == event.getPlayer().getUniqueId()){
                latestplayer.setLevel(latestplayer.getLevel() - 1);
                reloadborder = false;
            }
        }
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        JoinListener joinListener = new JoinListener();
        pluginManager.registerEvents(joinListener, this);

        DeathListener deathListener = new DeathListener();
        pluginManager.registerEvents(deathListener, this);

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
