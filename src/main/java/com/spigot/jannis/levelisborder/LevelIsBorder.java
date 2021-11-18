package com.spigot.jannis.levelisborder;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class LevelIsBorder extends JavaPlugin {
    private WorldBorderApi worldBorderApi;

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
            for (Player pl : players) {
                worldBorderApi.setBorder(pl, (all_levels * 1.8) + 3);
            }

        }
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        LevelListener listener = new LevelListener();
        pluginManager.registerEvents(listener, this);

        worldBorderApi = BorderAPI.getApi();
       /* RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            getLogger().info("API not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();*/
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
