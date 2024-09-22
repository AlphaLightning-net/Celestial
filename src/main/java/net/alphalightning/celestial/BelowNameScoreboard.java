package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;

import java.util.LinkedList;

public final class BelowNameScoreboard implements ScoreboardBase {

    private final LinkedList<Component> lines;
    private final Component title;

    BelowNameScoreboard(Component title, LinkedList<Component> lines) {
        this.title = title;
        this.lines = lines;
    }

    @Override
    public void display() {
    }
}
