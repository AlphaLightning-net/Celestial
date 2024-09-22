package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Scoreboard {

    void display();

    void destroy();

    interface Builder<T> {
        T title(@NotNull Component title);

        T appendLine(@NotNull Component line);

        T appendEmptyLine();

        T appendLines(@NotNull Collection<Component> lines);
    }

    static ScoreboardBase.Builder builder(@NotNull DisplayType type) {
        if (type == DisplayType.SIDEBAR) {
            return new SidebarScoreboardBuilder();
        }
        return new BelowNameScoreboardBuilder();
    }
}
