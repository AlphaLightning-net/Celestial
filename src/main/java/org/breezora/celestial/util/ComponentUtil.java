package org.breezora.celestial.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * A utility class for handling components
 *
 * @since 1.0.0
 */
public final class ComponentUtil {

    private ComponentUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Compares two components for pure quality after they have been serialized by {@link MiniMessage}
     *
     * @param first  The first component
     * @param second The second component
     * @return true if they are equal, otherwise false
     */
    public static boolean compareEquals(Component first, Component second) {
        var miniMessage = MiniMessage.miniMessage();

        var serializedFirst = miniMessage.serializeOr(first, "");
        var serializedSecond = miniMessage.serializeOr(second, "");

        return serializedFirst.equals(serializedSecond);
    }
}
