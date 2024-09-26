package net.alphalightning.celestial;

public final class Lifecycle {

    public enum Objective {
        CREATE, REMOVE, UPDATE
    }

    public enum Scoreboard {
        CHANGE, REMOVE
    }

    public enum Team {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

}
