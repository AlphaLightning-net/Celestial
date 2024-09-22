package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

final class BelowNameScoreboardBuilder implements ScoreboardBase.Builder {

    private final LinkedList<Component> lines = new LinkedList<>();
    private Component title;

    @Override
    public ScoreboardBase.Builder title(@NotNull Component title) {
        this.title = title;
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendLine(@NotNull Component line) {
        lines.add(line);
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendEmptyLine() {
        lines.add(Component.empty());
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendLines(@NotNull Collection<Component> lines) {
        this.lines.addAll(lines);
        return this;
    }

    @Override
    public ScoreboardBase build() {
        return new BelowNameScoreboard(title, lines);
    }
}
