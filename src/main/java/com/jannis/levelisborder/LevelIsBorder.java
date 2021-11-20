package com.jannis.levelisborder;

import com.github.yannicklamprecht.worldborder.api.IWorldBorder;
import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * The border size corresponds to the level of all players.
 *
 * @author Jannis
 */
public final class LevelIsBorder extends JavaPlugin {
    private WorldBorderApi worldBorderApi;
    private Player latestplayer;
    private boolean reloadborder = false;
    private Position pos_center = new Position(0.0,0.0);
    private double size = 0;
    private IWorldBorder worldBorder;

    public class JoinListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            player.setLevel(player.getLevel() + 1);
            latestplayer = player;
            reloadborder = true;
        }
    }

    public class DeathListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerDeathEvent event) {
            Player player = event.getEntity();
            player.setLevel(player.getLevel() + 1);
        }
    }

    public class LevelListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onEvent(PlayerLevelChangeEvent event) {
            Player player = event.getPlayer();
            World world = player.getWorld();
            List<Player> players = world.getPlayers();


            double playerSize = (1.8 * getSumOfPlayerLevels(world)) + 3;
            if(size > 0){
                playerSize = size + playerSize;
            }

            for (Player pl : players) {
                worldBorderApi.setBorder(pl, playerSize, pos_center);
            }
        }
    }

    public double getSumOfPlayerLevels(World world){
        List<Player> players = world.getPlayers();
        double sumofplayerlevels = 0;
        for (Player pl : players) {
            sumofplayerlevels = sumofplayerlevels + pl.getLevel();
        }
        return sumofplayerlevels;
    }

    public class MoveListener implements Listener {
        @EventHandler(priority = EventPriority.NORMAL)
        public void onEvent(PlayerMoveEvent event) {
            if (reloadborder && latestplayer.getUniqueId() == event.getPlayer().getUniqueId()) {
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

        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            getLogger().info("API not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            World world = player.getWorld();
            worldBorder = worldBorderApi.getWorldBorder(world);
            List<Player> players = world.getPlayers();
            if (args.length < 1){
                this.getLogger().info("Please add an argument");
            }
            else if (args[0].equals("center")){
                double x = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                pos_center = new Position(x, z);
                for (Player pl : players){
                    worldBorderApi.setBorder(pl, size + 1.8 * getSumOfPlayerLevels(world) + 3, pos_center);
                }
            }
            else if (args[0].equals("size")){
                size = Double.parseDouble(args[1]);

                for (Player pl : players){
                    worldBorderApi.setBorder(pl, size + 1.8 * getSumOfPlayerLevels(world) + 3,pos_center);
                }
            }
        }
        return true;
    }
}
