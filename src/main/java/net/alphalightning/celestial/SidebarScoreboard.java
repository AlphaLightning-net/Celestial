package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

final class SidebarScoreboard implements ScoreboardBase {

    private final Component title;
    private final LinkedList<Component> lines;
    private final LinkedList<Component> scores;

    SidebarScoreboard(Component title, LinkedList<Component> lines, LinkedList<Component> scores) {
        this.title = title;
        this.lines = lines;
        this.scores = scores;
    }

    @Override
    public void display() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void updateTitle(Component title) {
    }

    @Override
    public void updateLine(int line, Component text) {
    }

    @Override
    public void updateLine(int line, Component text, Component scoreText) {
    }

    @Override
    public void removeLine(int line) {
    }

    @Override
    public void updateLines(Component... lines) {
    }

    @Override
    public void updateLines(Collection<Component> lines) {
    }

    @Override
    public void updateLines(Collection<Component> lines, Collection<Component> scores) {
    }

    @Override
    public void updateScore(int line, Component text) {
    }

    @Override
    public void removeScore(int line) {
    }

    @Override
    public void updateScores(Component... texts) {
    }

    @Override
    public void updateScores(Collection<Component> texts) {
    }

    @Override
    public @NotNull Component title() {
        return title;
    }

    @Override
    public @UnmodifiableView @NotNull List<Component> lines() {
        return Collections.unmodifiableList(lines);
    }

    @Override
    public @Nullable Component line(int line) {
        return null;
    }

    @Override
    public @NotNull Optional<Component> score(int line) {
        return Optional.empty();
    }
}
