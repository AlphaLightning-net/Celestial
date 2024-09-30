package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public sealed interface ScoreboardBase extends Scoreboard permits SidebarScoreboard, BelowNameScoreboard {

    sealed interface Builder extends Scoreboard.Builder<Builder> permits SidebarScoreboardBuilder, BelowNameScoreboardBuilder {

        @Override
        Builder title(Component title);

        @Override
        Builder appendEmptyLine();

        @Override
        Builder appendLine(Component line);

        Builder appendLine(Component text, Component scoreText);

        @Override
        Builder appendLines(Collection<Component> lines);

        Builder appendLines(Collection<Component> lines, Collection<Component> scores);

        Builder player(Player player);

        @NotNull ScoreboardBase build();
    }
}
