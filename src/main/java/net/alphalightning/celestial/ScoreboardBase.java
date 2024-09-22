package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public sealed interface ScoreboardBase extends Scoreboard permits SidebarScoreboard, BelowNameScoreboard {

    @Override
    default void destroy() {
    }

    sealed interface Builder extends Scoreboard.Builder<Builder> permits SidebarScoreboardBuilder, BelowNameScoreboardBuilder {

        @Override
        Builder title(Component title);

        @Override
        Builder appendLine(Component line);

        @Override
        Builder appendEmptyLine();

        @Override
        Builder appendLines(Collection<Component> lines);

        @NotNull ScoreboardBase build();
    }
}
