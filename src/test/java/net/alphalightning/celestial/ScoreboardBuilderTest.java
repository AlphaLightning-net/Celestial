package net.alphalightning.celestial;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ScoreboardBuilderTest {

    @Test
    void testBuilderTypeSelection() {
        var builder = Scoreboard.builder(DisplayType.SIDEBAR);
        assertInstanceOf(SidebarScoreboardBuilder.class, builder);

        builder = Scoreboard.builder(DisplayType.BELOW_NAME);
        assertInstanceOf(BelowNameScoreboardBuilder.class, builder);
    }

}
