package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

final class BelowNameScoreboardBuilder implements ScoreboardBase.Builder {

    private final LinkedList<Component> lines = new LinkedList<>();
    private final LinkedList<Component> scores = new LinkedList<>();
    private Component title;

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
    public ScoreboardBase.Builder appendLine(Component line, Component scoreText) {
        if (line == null) {
            throw new IllegalArgumentException("The line cannot be null");
        }

        lines.add(line);
        scores.add(scoreText);
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendLine(Component line) {
        appendLine(line, null);
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendEmptyLine() {
        lines.add(Component.empty());
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendLines(Collection<Component> lines, Collection<Component> scoreTexts) {
        if (lines.contains(null)) {
            throw new IllegalArgumentException("A line cannot be null");
        }

        this.lines.addAll(lines);
        this.scores.addAll(scoreTexts);
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
    public @NotNull ScoreboardBase build() {
        return new BelowNameScoreboard(title, lines, scores);
    }
}
