package net.alphalightning.celestial;

/**
 * A class that contains lifecycles of various different objects
 *
 * @since 1.0.0
 */
public final class Lifecycle {

    /**
     * Lifecycle of a scoreboard's objective
     *
     * @since 1.0.0
     */
    public enum Objective {

        /**
         * The creation state
         */
        CREATE,

        /**
         * The remove state
         */
        REMOVE,

        /**
         * The update state
         */
        UPDATE
    }

    /**
     * Lifecycle of a scoreboard
     *
     * @since 1.0.0
     */
    public enum Scoreboard {

        /**
         * The change state
         */
        CHANGE,

        /**
         * The remove state
         */
        REMOVE
    }

    /**
     * Lifecycle of a scoreboard's team
     */
    public enum Team {

        /**
         * The creation state
         */
        CREATE,

        /**
         * The remove state
         */
        REMOVE,

        /**
         * The update state
         */
        UPDATE,

        /**
         * The adding player state
         */
        ADD_PLAYERS,

        /**
         * The removing player state
         */
        REMOVE_PLAYERS
    }

}
