package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Scoreboard {

    void display();

    void destroy();

    void updateTitle(Component title);

    void updateLine(int line, Component text);

    void updateLine(int line, Component text, Component scoreText);

    void removeLine(int line);

    void updateLines(Component... lines);

    void updateLines(Collection<Component> lines);

    void updateLines(Collection<Component> lines, Collection<Component> scores);

    void updateScore(int line, Component text);

    void removeScore(int line);

    void updateScores(Component... texts);

    void updateScores(Collection<Component> texts);

    @NotNull Component title();

    @UnmodifiableView
    @NotNull List<Component> lines();

    @ApiStatus.Experimental
    @Nullable Component line(int line);

    @ApiStatus.Experimental
    @NotNull Optional<Component> score(int line);

    interface Builder<T> {
        T title(Component title);

        T appendEmptyLine();

        T appendLine(Component line);

        T appendLines(Collection<Component> lines);
    }

    static ScoreboardBase.Builder builder(DisplayType type) {
        if (type == DisplayType.SIDEBAR) {
            return new SidebarScoreboardBuilder();
        }
        return new BelowNameScoreboardBuilder();
    }
}
