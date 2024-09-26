package net.alphalightning.celestial;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public interface ComponentComparable {

    default boolean compareEquals(Component first, Component second) {
        var miniMessage = MiniMessage.miniMessage();

        var serializedFirst = miniMessage.serialize(first);
        var serializedSecond = miniMessage.serialize(second);

        return serializedFirst.equals(serializedSecond);
    }

}
