package net.alphalightning.celestial.plugin;

import net.alphalightning.celestial.DisplayType;
import net.alphalightning.celestial.Scoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CelestialPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var miniMessage = MiniMessage.miniMessage();
        var scoreboard = Scoreboard.builder(DisplayType.SIDEBAR)
                .player(event.getPlayer())
                .title(miniMessage.deserialize("<green>Example title"))
                .appendEmptyLine()  // 0
                .appendLine(miniMessage.deserialize("<blue>Sehr lange Example line")) // 1
                .appendEmptyLine() // 2
                .appendEmptyLine() // 3
                .build();

        scoreboard.display();
        Bukkit.getScheduler().runTaskLater(this, () -> scoreboard.removeLine(3), 60L);
    }
}
