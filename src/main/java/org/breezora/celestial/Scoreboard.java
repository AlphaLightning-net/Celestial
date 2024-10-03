package org.breezora.celestial;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Represents an abstract definition of a scoreboard and the api structure.
 *
 * @since 1.0.0
 */
public interface Scoreboard {

    /**
     * Displays the scoreboard to the player
     */
    void display();

    /**
     * Destroys the scoreboard
     */
    void destroy();

    /**
     * Updates the scoreboard title
     *
     * @param title The new scoreboard title
     */
    void updateTitle(Component title);

    /**
     * Updates a single scoreboard line
     *
     * @param line The line number
     * @param text The new line text
     * @throws IndexOutOfBoundsException If the line number is higher than {@link #lines() size + 1}
     */
    void updateLine(int line, Component text);

    /**
     * Updates a single scoreboard line including its score
     *
     * @param line      The line number
     * @param text      The new line text
     * @param scoreText The new line's score, if null will not change current value
     * @throws IndexOutOfBoundsException If the line is higher than {@link #lines() size + 1}
     */
    void updateLine(int line, Component text, Component scoreText);

    /**
     * Remove a scoreboard line
     *
     * @param line The line number
     */
    void removeLine(int line);

    /**
     * Update all scoreboard lines
     *
     * @param lines The new lines
     */
    void updateLines(Component... lines);

    /**
     * Update all scoreboard lines
     *
     * @param lines The new lines
     */
    void updateLines(Collection<Component> lines);

    /**
     * Update the lines and how their score is displayed on the scoreboard
     *
     * @param lines  The new scoreboard lines
     * @param scores The Set for how each line's score should be, if null will fall back to default (blank)
     * @throws IllegalArgumentException If lines and scores are not the same size or no lines are passed
     */
    void updateLines(Collection<Component> lines, Collection<Component> scores);

    /**
     * Update how a specified line's score is displayed on the scoreboard. A null value will reset the displayed text back to default (blank)
     *
     * @param line The line number
     * @param text The text to be displayed as the score
     */
    void updateScore(int line, Component text);

    /**
     * Resets a line's score back to default (blank)
     *
     * @param line The line number
     * @throws IllegalArgumentException If the line number is not in range
     */
    void removeScore(int line);

    /**
     * Updates how all lines' scores are displayed. A value of null will reset the displayed text back to default.
     *
     * @param texts The set of texts to be displayed as the scores
     */
    void updateScores(Component... texts);

    /**
     * Update how all lines' scores are displayed.  A null value will reset the displayed text back to default (blank)
     *
     * @param texts The set of texts to be displayed as the scores
     * @throws IllegalArgumentException If the size of the texts does not match the current size of the board
     */
    void updateScores(Collection<Component> texts);

    /**
     * Gets the current title of the scoreboard
     *
     * @return The title
     */
    @NotNull Component title();

    /**
     * Gets an immutable copy of the current scoreboard lines.
     *
     * @return The current lines
     */
    @UnmodifiableView
    @NotNull List<Component> lines();

    /**
     * Gets the specified line. May change later due to experimental state
     *
     * @param line The line number
     * @return The line
     * @throws IllegalArgumentException If the line number is invalid
     */
    @ApiStatus.Experimental
    @Nullable Component line(int line);

    /**
     * Gets the score of a specified scoreboard line. May change later due to experimental state
     *
     * @param line The line number
     * @return The score
     * @throws IllegalArgumentException If the line number is invalid
     */
    @ApiStatus.Experimental
    @NotNull Optional<Component> score(int line);

    /**
     * Represents an abstract definition of a scoreboard builder.
     *
     * @param <T> The explicit type
     * @since 1.0.0
     */
    interface Builder<T> {

        /**
         * Defines the initial scoreboard title
         *
         * @param title The new title
         * @return The builder
         */
        T title(Component title);

        /**
         * Adds an empty line to the future scoreboard
         *
         * @return The builder
         */
        T appendEmptyLine();

        /**
         * Adds a single line with a default (blank) score to the future scoreboard
         *
         * @param line The line
         * @return The builder
         */
        T appendLine(Component line);

        /**
         * Adds multiple lines with a default (blank) score to the future scoreboard
         *
         * @param lines The set of new scoreboard lines
         * @return The builder
         */
        T appendLines(Collection<Component> lines);
    }

    /**
     * Creates a fresh builder for the specified type of scoreboard
     *
     * @param type The scoreboard type
     * @return A fresh instance of a builder
     */
    static ScoreboardBase.Builder builder(DisplayType type) {
        if (type == DisplayType.SIDEBAR) {
            return new SidebarScoreboardBuilder();
        }
        throw new UnsupportedOperationException("The below name scoreboard is currently not supported");
    }
}
