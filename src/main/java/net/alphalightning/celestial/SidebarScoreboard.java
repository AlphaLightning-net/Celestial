package net.alphalightning.celestial;

import net.alphalightning.celestial.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

final class SidebarScoreboard extends ReflectiveScoreboardBase implements ScoreboardBase {

    private final LinkedList<Component> scores = new LinkedList<>();
    private final LinkedList<Component> lines;
    private final String name;
    private Component title;

    SidebarScoreboard(Player player, Component title, LinkedList<Component> lines) {
        super(player);
        this.title = title;
        this.lines = lines;
        this.name = "celestial-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());
    }

    @Override
    public void display() {
        try {
            sendObjectivePacket(Lifecycle.Objective.CREATE, name, title);
            sendDisplayObjectivePacket(name);

        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to create scoreboard", throwable);
        }
    }

    @Override
    public void destroy() {
        try {
            for (var i = 0; i < lines.size(); i++) {
                sendTeamPacket(name, i, Lifecycle.Team.REMOVE);
            }
            sendObjectivePacket(Lifecycle.Objective.REMOVE, name, title);

        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to destroy scoreboard", throwable);
        }
    }

    @Override
    public void updateTitle(Component title) {
        if (ComponentUtil.compareEquals(this.title, title)) return;

        this.title = title;
        try {
            sendObjectivePacket(Lifecycle.Objective.UPDATE, name, title);

        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update title", throwable);
        }
    }

    @Override
    public void updateLine(int line, Component text) {
        updateLine(line, text, null);
    }

    @Override
    public void updateLine(int line, Component text, Component scoreText) {
    }

    @Override
    public void removeLine(int line) {
    }

    @Override
    public void updateLines(Component... lines) {
        updateLines(Arrays.asList(lines));
    }

    @Override
    public void updateLines(Collection<Component> lines) {
        updateLines(lines, null);
    }

    @Override
    public synchronized void updateLines(Collection<Component> lines, Collection<Component> scores) {
        if (lines == null) throw new IllegalArgumentException("lines cannot be null");
        validateLine(lines.size(), false, true);

        if (scores != null && scores.size() != lines.size()) {
            throw new IllegalArgumentException("scores and lines must be the same size");
        }

        var oldLines = new ArrayList<>(this.lines);
        this.lines.clear();
        this.lines.addAll(lines);

        var oldScores = new ArrayList<>(this.scores);
        this.scores.clear();
        this.scores.addAll(scores != null ? scores : Collections.nCopies(lines.size(), null));

        var linesSize = this.lines.size();
        try {
            if (oldLines.size() != linesSize) {
                var oldLinesCopy = new ArrayList<>(oldLines);

                if (oldLines.size() > linesSize) {
                    for (var i = oldLinesCopy.size(); i > linesSize; i--) {
                        sendTeamPacket(name, i - 1, Lifecycle.Team.REMOVE);
                        sendScorePacket(name, this.scores, i - 1, Lifecycle.Scoreboard.REMOVE);
                        oldLines.removeFirst();
                    }
                } else {
                    for (var i = oldLinesCopy.size(); i < linesSize; i++) {
                        sendScorePacket(name, this.scores, i, Lifecycle.Scoreboard.CHANGE);
                        sendTeamPacket(name, i, Lifecycle.Team.CREATE);
                    }
                }
            }

            for (var i = 0; i < linesSize; i++) {
                if (!ComponentUtil.compareEquals(lineByScore(oldLines, i), lineByScore(this.lines, i))) {
                    sendLineChange(i);
                }
                if (!ComponentUtil.compareEquals(lineByScore(oldScores, i), lineByScore(this.scores, i))) {
                    sendScorePacket(name, this.scores, i, Lifecycle.Scoreboard.CHANGE);
                }
            }

        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update lines", throwable);
        }
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
    public synchronized void updateScores(Collection<Component> texts) {
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
        validateLine(line, true, false);
        return lines.get(line);
    }

    @Override
    public @NotNull Optional<Component> score(int line) {
        validateLine(line, true, false);
        return Optional.ofNullable(scores.get(line));
    }

    private void validateLine(int line, boolean checkInRange, boolean checkMax) {
        if (line < 0) {
            throw new IllegalArgumentException("Line cannot be negative");
        }
        if (checkInRange && line >= this.lines.size()) {
            throw new IllegalArgumentException("Line cannot be greater than the number of lines (%s)".formatted(lines.size()));
        }
        if (checkMax && line >= LEGACY_COLOR_CODES.length - 1) {
            throw new IllegalArgumentException("Line number is too high: " + line);
        }
    }

    private void sendLineChange(int score) throws Throwable {
        var line = lineByScore(this.lines, score);
        sendTeamPacket(name, score, Lifecycle.Team.UPDATE, line, null);
    }
}