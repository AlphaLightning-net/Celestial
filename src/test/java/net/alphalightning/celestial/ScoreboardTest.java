package net.alphalightning.celestial;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.alphalightning.celestial.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardTest {

    private ServerMock server;
    private PlayerMock player;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        player = server.addPlayer();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        player = null;
    }

    @Test
    void testScoreboardCreation() {
        var scoreboard = Scoreboard.builder(DisplayType.SIDEBAR)
                .player(player)
                .build();
        assertInstanceOf(SidebarScoreboard.class, scoreboard);

        scoreboard = Scoreboard.builder(DisplayType.BELOW_NAME).build();
        assertInstanceOf(BelowNameScoreboard.class, scoreboard);
    }

    @Test
    void testTitle() {
        var scoreboard = Scoreboard.builder(DisplayType.SIDEBAR)
                .player(player)
                .title(MiniMessage.miniMessage().deserialize("<green>Example Title"))
                .build();

        assertTrue(ComponentUtil.compareEquals(scoreboard.title(), MiniMessage.miniMessage().deserialize("<green>Example Title")));
    }

    @Test
    void testLines() {
        var scoreboard = Scoreboard.builder(DisplayType.SIDEBAR)
                .player(player)
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
    }
}
