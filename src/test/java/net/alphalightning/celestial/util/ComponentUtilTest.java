package net.alphalightning.celestial.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentUtilTest {

    @Test
    void testEquals() {
        var miniMessage = MiniMessage.miniMessage();

        var first = miniMessage.deserialize("<gold>Sehr lange Example line");
        var second = miniMessage.deserialize("<gold>Sehr lange Example line");

        assertTrue(ComponentUtil.compareEquals(first, second));
    }

}