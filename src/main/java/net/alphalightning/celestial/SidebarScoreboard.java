package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class SidebarScoreboard implements ScoreboardBase {

    private final Component title;
    private final LinkedList<Component> lines;

    SidebarScoreboard(Component title, LinkedList<Component> lines) {
        this.title = title;
        this.lines = lines;
    }

    @Override
    public void display() {
    }

    @Override
    public @NotNull Component title() {
        return title;
    }

    @Override
    public @UnmodifiableView @NotNull List<Component> lines() {
        return Collections.unmodifiableList(lines);
    }

}
