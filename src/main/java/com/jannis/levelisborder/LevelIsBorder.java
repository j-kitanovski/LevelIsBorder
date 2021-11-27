package com.jannis.levelisborder;

import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The border size corresponds to the level of all players.
 *
 * @author Jannis
 */
public final class LevelIsBorder extends JavaPlugin {
    private WorldBorderApi worldBorderApi;
    private Player latestPlayer;
    private boolean reloadBorder = false;
    private Position posCenter;
    private double size = 3;
    private File file;
    private boolean reloadInProgress = false;
    private YamlConfiguration config;

    public class JoinListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            player.setLevel(player.getLevel() + 1);
            latestPlayer = player;
            reloadBorder = true;
        }
    }

    public static class DeathListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerDeathEvent event) {
            Player player = event.getEntity();
            player.setLevel(player.getLevel() + 1);
        }
    }

    public class ChangeWorldListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEvent(PlayerChangedWorldEvent event) {

            Player player = event.getPlayer();
            World world = player.getWorld();
            Advancement nether_adv = getServer().getAdvancement(NamespacedKey.minecraft("story/enter_the_nether"));
            Advancement end_adv = getServer().getAdvancement(NamespacedKey.minecraft("story/enter_the_end"));
            Position position = new Position(player.getLocation().getX(), player.getLocation().getZ());
            Location telePos = new Location(world, posCenter.x(), world.getHighestBlockYAt((int) Math.round(posCenter.x()), (int) Math.round(posCenter.z())) + 1, posCenter.z());
            if (world.getName().endsWith("nether") && nether_adv != null) {
                if (player.getAdvancementProgress(nether_adv).isDone()) {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        worldBorderApi.setBorder(pl, size, position);
                    }
                }
            } else if (world.getName().endsWith("end") && end_adv != null) {
                if (player.getAdvancementProgress(end_adv).isDone()) {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        worldBorderApi.setBorder(pl, size, position);
                    }
                }
            } else if (!world.getName().endsWith("end") && !world.getName().endsWith("nether")) {
                if (event.getFrom().getName().endsWith("end")) {
                    player.teleport(telePos);
                }
                player.setLevel(player.getLevel() + 1);
                reloadBorder = true;
                latestPlayer = player;
            }
        }
    }

    public class LevelListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onEvent(PlayerLevelChangeEvent event) {
            Player player = event.getPlayer();
            World world = player.getWorld();
            List<Player> players = world.getPlayers();


            double playerSize = (calculateSize(0, world));
            if (size >= 0) {
                playerSize = size + playerSize;
            }

            for (Player pl : players) {
                worldBorderApi.setBorder(pl, playerSize, posCenter);
            }
        }
    }

    public class MoveListener implements Listener {
        @EventHandler(priority = EventPriority.NORMAL)
        public void onEvent(PlayerMoveEvent event) {
            if (reloadBorder && latestPlayer.getUniqueId() == event.getPlayer().getUniqueId() && reloadInProgress) {
                latestPlayer.setLevel(latestPlayer.getLevel() - 1);
                reloadBorder = false;
                reloadInProgress = false;
            }
        }
    }

    private double getSumOfPlayerLevels(World world) {
        List<Player> players = world.getPlayers();
        double sumOfPlayerLevels = 0;
        for (Player pl : players) {
            sumOfPlayerLevels = sumOfPlayerLevels + pl.getLevel();
        }
        return sumOfPlayerLevels;
    }

    private double calculateSize(double size, World world) {
        return size + 1.8 * getSumOfPlayerLevels(world);
    }

    private void initConfigFile() {
        this.saveResource("config.yml", false);
        file = new File(this.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        //set data
        size = config.getDouble("size");
        posCenter = new Position(config.getDouble("center.x"), config.getDouble("center.z"));
    }

    private void saveConfigData(double x, double z) throws IOException {
        config.set("center.x", x);
        config.set("center.z", z);
        config.save(file);
    }

    private void saveConfigData(double size) throws IOException {
        config.set("size", size);
        config.save(file);
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        JoinListener joinListener = new JoinListener();
        pluginManager.registerEvents(joinListener, this);

        DeathListener deathListener = new DeathListener();
        pluginManager.registerEvents(deathListener, this);

        ChangeWorldListener changeWorldListener = new ChangeWorldListener();
        pluginManager.registerEvents(changeWorldListener, this);

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
        initConfigFile();
        reloadInProgress = true;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = player.getWorld();
            List<Player> players = world.getPlayers();
            if (args.length < 1) {
                this.getLogger().info("§5Please add an argument");
            } else if (args[0].equals("center")) {
                try {
                    double x;
                    double z;
                    if (args[1].equals("~") && args[2].equals("~")) {
                        x = player.getLocation().getX();
                        z = player.getLocation().getZ();
                    } else if (args[1].equals("~")) {
                        x = player.getLocation().getX();
                        z = Double.parseDouble(args[2]);
                    } else if (args[2].equals("~")) {
                        x = Double.parseDouble(args[1]);
                        z = player.getLocation().getZ();
                    } else {
                        x = Double.parseDouble(args[1]);
                        z = Double.parseDouble(args[2]);
                    }
                    saveConfigData(x, z);
                    posCenter = new Position(x, z);
                    for (Player pl : players) {
                        worldBorderApi.setBorder(pl, calculateSize(size, world), posCenter);
                    }
                } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e1) {
                    player.sendMessage("§5You have to enter two numbers.");
                } catch (IOException e1) {
                    this.getLogger().info("Unable to safe configuration file.");
                }

            } else if (args[0].equals("size")) {
                try {
                    size = Double.parseDouble(args[1]);
                    for (Player pl : players) {
                        worldBorderApi.setBorder(pl, calculateSize(size, world), posCenter);
                    }
                    saveConfigData(size);
                } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e1) {
                    player.sendMessage("§5You have to enter a number as argument.");
                } catch (IOException e1) {
                    this.getLogger().info("Unable to safe configuration file.");
                }
            } else if (args[0].equals("reload")) {
                if (!reloadInProgress) {
                    player.setLevel(player.getLevel() + 1);
                    reloadBorder = true;
                    latestPlayer = player;
                    reloadInProgress = true;
                    Location tp_location = new Location(world, player.getLocation().getX(), player.getLocation().getY() + 0.7, player.getLocation().getZ());
                    player.teleport(tp_location);
                } else {
                    player.sendMessage("§5You have already started a reload. Move to stop it.");
                }
            } else {
                player.sendMessage("§5Enter an argument. Type /help border to see all.");
            }
        }
        return true;
    }
}
