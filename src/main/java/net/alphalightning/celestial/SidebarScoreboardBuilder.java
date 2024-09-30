package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents a concrete implementation of a {@link ScoreboardBase.Builder}
 *
 * @since 1.0.0
 */
final class SidebarScoreboardBuilder implements ScoreboardBase.Builder {

    private final LinkedList<Component> lines = new LinkedList<>();
    private final LinkedList<Component> scores = new LinkedList<>();
    private Component title;

    private Player player;

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardBase.Builder title(Component title) {
        if (title == null) {
            throw new IllegalArgumentException("The title cannot be null");
        }
        if (this.title != null) {
            throw new IllegalArgumentException("The title is already set");
        }

        this.title = title;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardBase.Builder appendEmptyLine() {
        appendLine(Component.empty());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardBase.Builder appendLine(Component line) {
        appendLine(line, null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardBase.Builder appendLine(Component text, Component scoreText) {
        if (text == null) {
            throw new IllegalArgumentException("The line cannot be null");
        }

        lines.add(text);
        scores.add(scoreText);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardBase.Builder appendLines(Collection<Component> lines) {
        appendLines(lines, null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardBase.Builder appendLines(Collection<Component> lines, Collection<Component> scores) {
        if (lines.contains(null)) {
            throw new IllegalArgumentException("A line cannot be null");
        }

        this.lines.addAll(lines);
        if (scores == null || scores.isEmpty()) {
            for (var i = 0; i < lines.size(); i++) {
                this.scores.add(null);
            }
        } else {
            this.scores.addAll(scores);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardBase.Builder player(Player player) {
        this.player = player;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ScoreboardBase build() {
        if (player == null) throw new IllegalStateException("The player cannot be null");
        return new SidebarScoreboard(player, title, lines, scores);
    }
}
