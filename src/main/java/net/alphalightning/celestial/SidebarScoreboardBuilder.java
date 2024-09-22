package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

final class SidebarScoreboardBuilder implements ScoreboardBase.Builder {

    @Override
    public ScoreboardBase.Builder type(@NotNull DisplayType type) {
        return this;
    }

    @Override
    public ScoreboardBase.Builder title(@NotNull Component title) {
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendLine(@NotNull Component line) {
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendEmptyLine() {
        return this;
    }

    @Override
    public ScoreboardBase.Builder appendLines(@NotNull Collection<Component> lines) {
        return this;
    }

    @Override
    public ScoreboardBase build() {
        return null;
    }
}
