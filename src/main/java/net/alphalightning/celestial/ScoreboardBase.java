package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents a more detailed definition of a scoreboard. Defines the types of publicly visible scoreboards
 *
 * @since 1.0.0
 */
public sealed interface ScoreboardBase extends Scoreboard permits SidebarScoreboard, BelowNameScoreboard {

    /**
     * Represents a more detailed definition of a scoreboard builder. Defines the publicly visible types of builders
     */
    sealed interface Builder extends Scoreboard.Builder<Builder> permits SidebarScoreboardBuilder, BelowNameScoreboardBuilder {

        /**
         * {@inheritDoc}
         */
        @Override
        Builder title(Component title);

        /**
         * {@inheritDoc}
         */
        @Override
        Builder appendEmptyLine();

        /**
         * {@inheritDoc}
         */
        @Override
        Builder appendLine(Component line);

        /**
         * Adds the future scoreboard line and its score to the builder. A null score text will reset it back to its default (blank)
         *
         * @param text      The line text
         * @param scoreText The score text
         * @return The builder
         */
        Builder appendLine(Component text, Component scoreText);

        /**
         * {@inheritDoc}
         */
        @Override
        Builder appendLines(Collection<Component> lines);

        /**
         * Adds the future scoreboard lines and their scores to the builder. A null list entry for scores will reset it to its default (blank)
         *
         * @param lines  The set of new lines
         * @param scores The set of the lines scores
         * @return The builder
         */
        Builder appendLines(Collection<Component> lines, Collection<Component> scores);

        /**
         * Specifies the player to whom the scoreboard will be displayed. Necessary for the builder to work
         *
         * @param player The player
         * @return The builder
         */
        Builder player(Player player);

        /**
         * Instantiates a new scoreboard
         *
         * @return The scoreboard
         * @throws IllegalStateException If the player or title is null
         */
        @NotNull ScoreboardBase build();
    }
}
