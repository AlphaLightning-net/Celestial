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
                .appendEmptyLine()
                .appendLine(miniMessage.deserialize("<blue>Sehr lange Example line"))
                .appendEmptyLine()
                .build();

        scoreboard.display();
        Bukkit.getScheduler().runTaskLater(this, () -> scoreboard.updateLines(
                Component.empty(),
                miniMessage.deserialize("<gold>Sehr lange example line"),
                Component.empty()
        ), 60L);
    }
}
