package com.spigot.jannis.levelisborder;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class LevelIsBorder extends JavaPlugin {
    public class LevelListener implements Listener {
        @EventHandler
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
            border.setSize((all_levels + 3) * 1.8);
        }
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        LevelListener listener = new LevelListener();
        pluginManager.registerEvents(listener, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
