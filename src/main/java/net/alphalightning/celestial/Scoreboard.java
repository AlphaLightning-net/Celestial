package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;

public interface Scoreboard {

    void display();

    void destroy();

    @NotNull Component title();

    @UnmodifiableView
    @NotNull List<Component> lines();

    interface Builder<T> {
        T title(Component title);

        T appendLine(Component line);

        T appendEmptyLine();

        T appendLines(Collection<Component> lines);
    }

    static ScoreboardBase.Builder builder(DisplayType type) {
        if (type == DisplayType.SIDEBAR) {
            return new SidebarScoreboardBuilder();
        }
        return new BelowNameScoreboardBuilder();
    }
}
