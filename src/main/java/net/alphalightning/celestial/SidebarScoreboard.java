package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;

import java.util.LinkedList;

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

}
