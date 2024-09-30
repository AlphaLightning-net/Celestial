package net.alphalightning.celestial;

import net.alphalightning.celestial.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a concrete implementation of a {@link ScoreboardBase}
 *
 * @see ReflectiveScoreboardBase
 * @since 1.0.0
 */
final class SidebarScoreboard extends ReflectiveScoreboardBase implements ScoreboardBase {

    private final LinkedList<Component> scores;
    private final LinkedList<Component> lines;
    private final String name;
    private Component title;

    /**
     * Creates a new scoreboard from the builder and defines a unique name
     *
     * @param player The player
     * @param title  The title
     * @param lines  The lines
     * @param scores The scores
     */
    SidebarScoreboard(Player player, Component title, LinkedList<Component> lines, LinkedList<Component> scores) {
        super(player);
        this.title = title;
        this.lines = lines;
        this.scores = scores;
        this.name = "celestial-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void display() {
        try {
            sendObjectivePacket(Lifecycle.Objective.CREATE, name, title);
            sendDisplayObjectivePacket(name);
            sendInitialLines();

        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to create scoreboard", throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLine(int line, Component text) {
        updateLine(line, text, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLine(int line, Component text, Component scoreText) {
        validateLine(line, false, false);

        try {
            if (line < lines.size()) {
                lines.set(line, text);
                if (!scores.isEmpty()) scores.set(line, scoreText);

                var score = scoreByLine(line);
                sendLineChange(score);
                sendScorePacket(name, scores, score, Lifecycle.Scoreboard.CHANGE);

                return;
            }

            var newLines = new ArrayList<>(lines);
            var newScores = new ArrayList<>(scores);

            if (line > lines.size()) {
                for (var i = lines.size(); i < line; i++) {
                    newLines.add(Component.empty());
                    newScores.add(null);
                }
            }

            newLines.add(text);
            newScores.add(scoreText);

            updateLines(newLines, newScores);

        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update line", throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeLine(int line) {
        validateLine(line, false, false);
        if (line >= lines.size()) return;

        var newLines = new ArrayList<>(lines);
        var newScores = new ArrayList<>(scores);
        if (!scores.isEmpty()) {
            newScores.remove(line);
        }
        newLines.remove(line);

        updateLines(newLines, scores.isEmpty() ? null : newScores);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLines(Component... lines) {
        updateLines(Arrays.asList(lines));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLines(Collection<Component> lines) {
        updateLines(lines, null);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void updateScore(int line, Component text) {
        validateLine(line, true, false);

        scores.set(line, text);
        try {
            sendScorePacket(name, scores, scoreByLine(line), Lifecycle.Scoreboard.CHANGE);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update score", throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeScore(int line) {
        updateScore(line, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateScores(Component... texts) {
        updateScores(Arrays.asList(texts));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void updateScores(Collection<Component> texts) {
        if (texts == null) {
            throw new IllegalArgumentException("Scores cannot be null");
        }
        if (scores.size() != lines.size()) {
            throw new IllegalArgumentException("Scores and lines must be the same size");
        }

        var newScores = new ArrayList<>(texts);
        for (var i = 0; i < scores.size(); i++) {
            if (Objects.equals(scores.get(i), newScores.get(i))) continue;

            scores.set(i, newScores.get(i));

            try {
                sendScorePacket(name, scores, i, Lifecycle.Scoreboard.CHANGE);
            } catch (Throwable throwable) {
                throw new RuntimeException("Unable to update scores", throwable);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Component title() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @UnmodifiableView @NotNull List<Component> lines() {
        return Collections.unmodifiableList(lines);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Component line(int line) {
        validateLine(line, true, false);
        return lines.get(line);
    }

    /**
     * {@inheritDoc}
     */
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

    private void sendInitialLines() throws Throwable {
        for (var i = 0; i < lines.size(); i++) {
            sendScorePacket(name, this.scores, i, Lifecycle.Scoreboard.CHANGE);
            sendTeamPacket(name, i, Lifecycle.Team.CREATE);
            sendLineChange(i);
        }
    }

    private int scoreByLine(int line) {
        return lines.size() - line - 1;
    }

}