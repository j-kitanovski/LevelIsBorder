package com.jannis.levelisborder.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.setLevel(player.getLevel() + 1);
    }
}
