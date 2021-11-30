package com.jannis.levelisborder.listener;

import com.github.yannicklamprecht.worldborder.api.Position;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.jannis.levelisborder.LevelIsBorder;
import com.jannis.levelisborder.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ChangeWorldListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(PlayerChangedWorldEvent event) {
        //get vars from LevelIsBorder
        LevelIsBorder levelIsBorder = (LevelIsBorder) Bukkit.getPluginManager().getPlugin(LevelIsBorder.PLUGIN_NAME);
        WorldBorderApi worldBorderApi = levelIsBorder.getWorldBorderApi();
        Position posCenter = levelIsBorder.getPosCenter();
        double size = levelIsBorder.getSize();
        //init and declare vars
        Player player = event.getPlayer();
        World world = player.getWorld();
        Advancement nether_adv = Bukkit.getAdvancement(NamespacedKey.minecraft("story/enter_the_nether"));
        Advancement end_adv = Bukkit.getAdvancement(NamespacedKey.minecraft("story/enter_the_end"));
        Position position = new Position(player.getLocation().getX(), player.getLocation().getZ());
        Location telePos = new Location(world, posCenter.x(), world.getHighestBlockYAt((int) Math.round(posCenter.x()), (int) Math.round(posCenter.z())) + 1, posCenter.z());

        if (Util.isNether(world) && nether_adv != null) {
            levelIsBorder.setPositionInMap(position, world);
            if (player.getAdvancementProgress(nether_adv).isDone()) {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    worldBorderApi.setBorder(pl, size, position);
                }
            }
        } else if (Util.isTheEnd(world) && end_adv != null) {
            levelIsBorder.setPositionInMap(position, world);
            if (player.getAdvancementProgress(end_adv).isDone()) {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    worldBorderApi.setBorder(pl, size, position);
                }
            }
        } else {
            if (event.getFrom().getName().endsWith("end")) {
                player.teleport(telePos);
            }
            player.setLevel(player.getLevel() + 1);
            levelIsBorder.setReloadBorder(true);
            levelIsBorder.setLatestPlayer(player);
        }
    }
}
