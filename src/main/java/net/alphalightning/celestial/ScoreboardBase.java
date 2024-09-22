package net.alphalightning.celestial;

public sealed interface ScoreboardBase extends Scoreboard permits SidebarScoreboard, BelowNameScoreboard {

    @Override
    default void destroy() {
    }
}
