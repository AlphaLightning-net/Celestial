package net.alphalightning.celestial;

import net.alphalightning.celestial.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardTest {

    @Test
    void testScoreboardCreation() {
        var scoreboard = Scoreboard.builder(DisplayType.SIDEBAR).build();
        assertInstanceOf(SidebarScoreboard.class, scoreboard);

        scoreboard = Scoreboard.builder(DisplayType.BELOW_NAME).build();
        assertInstanceOf(BelowNameScoreboard.class, scoreboard);
    }

    @Test
    void testTitle() {
        var scoreboard = Scoreboard.builder(DisplayType.SIDEBAR)
                .title(MiniMessage.miniMessage().deserialize("<green>Example Title"))
                .build();

        assertTrue(ComponentUtil.compareEquals(scoreboard.title(), MiniMessage.miniMessage().deserialize("<green>Example Title")));
    }

    @Test
    void testLines() {
        var scoreboard = Scoreboard.builder(DisplayType.SIDEBAR)
                .appendEmptyLine()
                .appendLine(MiniMessage.miniMessage().deserialize("<red>Example Line"))
                .build();

        var lines = scoreboard.lines();

        assertEquals(2, lines.size());
        assertEquals(Component.empty(), lines.getFirst());
        assertTrue(ComponentUtil.compareEquals(lines.getLast(), MiniMessage.miniMessage().deserialize("<red>Example Line")));
    }


    @Test
    @SuppressWarnings("All")
    void testNullRequirements() {
        var builder = Scoreboard.builder(null);
        assertNotNull(builder); // There is no not null validation needed during code execution since the builder returns a BelowNameScoreboardBuilder if null was given

        var scoreboard = builder.title(null).build();
        assertNull(scoreboard.title()); // Since the scoreboard does need a title, a non-null validation might be useful to prevent further complications

        //TODO: Add non-null validation for titles and lines
    }
}
