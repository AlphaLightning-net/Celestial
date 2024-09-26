package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

final class SidebarScoreboard extends ReflectiveScoreboardBase implements ScoreboardBase {

    private final Component title;
    private final LinkedList<Component> lines;
    private final String name;

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
        return null;
    }

    @Override
    public @NotNull Optional<Component> score(int line) {
        return Optional.empty();
    }
}
