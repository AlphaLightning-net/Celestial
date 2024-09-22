package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Scoreboard {

    void display();

    void destroy();

    interface Builder<T> {
        T type(@NotNull DisplayType type);

        T title(@NotNull Component title);

        T appendLine(@NotNull Component line);

        T appendEmptyLine();

        T appendLines(@NotNull Collection<Component> lines);
    }
}
