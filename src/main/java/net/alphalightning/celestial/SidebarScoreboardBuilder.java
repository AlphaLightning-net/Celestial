package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

final class SidebarScoreboardBuilder implements ScoreboardBase.Builder {

    private final LinkedList<Component> lines = new LinkedList<>();
    private Component title;

    private Player player;

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

    @Override
    public ScoreboardBase.Builder appendLine(Component line) {
        if (line == null) {
            throw new IllegalArgumentException("The line cannot be null");
        }

        lines.add(line);
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendEmptyLine() {
        lines.add(Component.empty());
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendLines(Collection<Component> lines) {
        if (lines.contains(null)) {
            throw new IllegalArgumentException("A line cannot be null");
        }

        this.lines.addAll(lines);
        return this;
    }

    @Override
    public ScoreboardBase.@NotNull Builder player(Player player) {
        this.player = player;
        return this;
    }

    @Override
    public @NotNull ScoreboardBase build() {
        if (player == null) throw new IllegalStateException("The player cannot be null");
        return new SidebarScoreboard(player, title, lines);
    }
}
