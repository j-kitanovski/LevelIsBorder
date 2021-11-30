package com.jannis.levelisborder;

import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.jannis.levelisborder.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The border size corresponds to the level of all players.
 *
 * @author Jannis
 */
public final class LevelIsBorder extends JavaPlugin {


    private WorldBorderApi worldBorderApi;
    private Player latestPlayer;
    private boolean reloadBorder = false;
    private final Map<String, Position> positionCenter = new HashMap<>();
    private double size = 1;
    private File file;
    private boolean reloadInProgress = false;
    private YamlConfiguration config;
    public static final String PLUGIN_NAME = "LevelIsBorder";

    public double getSumOfPlayerLevels() {
        double sumOfPlayerLevels = 0;
        for (Player pl : Bukkit.getOnlinePlayers()) {
            sumOfPlayerLevels = sumOfPlayerLevels + pl.getLevel();
        }
        return sumOfPlayerLevels;
    }

    public double calculateSize(double size) {
        return size + 1.3 * getSumOfPlayerLevels();
    }

    private void initConfigFile() {
        this.saveResource("config.yml", false);
        file = new File(this.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        //set data
        size = config.getDouble("size");

        positionCenter.put(Util.OVER_WORLD, new Position(config.getDouble("center.over_world.x"), config.getDouble("center.over_world.z")));
        positionCenter.put(Util.NETHER, new Position(config.getDouble("center.nether.x"), config.getDouble("center.nether.z")));
        positionCenter.put(Util.THE_END, new Position(config.getDouble("center.the_end.x"), config.getDouble("center.the_end.z")));
    }

    private void saveConfigData(double x, double z, String worldType) throws IOException {
        config.set("center." + worldType + ".x", x);
        config.set("center." + worldType + ".z", z);
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
                    saveConfigData(x, z, Util.getWorldType(world));
                    Position newPosCenter = new Position(x, z);
                    positionCenter.put(Util.getWorldType(world), newPosCenter);
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        worldBorderApi.setBorder(pl, calculateSize(size), newPosCenter);
                    }
                } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e1) {
                    player.sendMessage("§5You have to enter two numbers.");
                } catch (IOException e1) {
                    this.getLogger().info("Unable to safe configuration file.");
                }

            } else if (args[0].equals("size")) {
                try {
                    size = Double.parseDouble(args[1]);
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        worldBorderApi.setBorder(pl, calculateSize(size), positionCenter.get(Util.getWorldType(world)));
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

    public Player getLatestPlayer() {
        return latestPlayer;
    }

    public void setLatestPlayer(Player latestPlayer) {
        this.latestPlayer = latestPlayer;
    }

    public boolean isReloadBorder() {
        return reloadBorder;
    }

    public void setReloadBorder(boolean reloadBorder) {
        this.reloadBorder = reloadBorder;
    }

    public WorldBorderApi getWorldBorderApi() {
        return worldBorderApi;
    }

    public Position getPosCenter() {
        return positionCenter.get(Util.getWorldType(latestPlayer.getWorld()));
    }

    public double getSize() {
        return size;
    }

    public boolean isReloadInProgress() {
        return reloadInProgress;
    }

    public void setReloadInProgress(boolean reloadInProgress) {
        this.reloadInProgress = reloadInProgress;
    }

    public void setPositionInMap(Position position, World world) {
        positionCenter.put(Util.getWorldType(world), position);
    }
}
