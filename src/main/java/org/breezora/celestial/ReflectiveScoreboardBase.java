package org.breezora.celestial;

import org.breezora.celestial.util.Reflections;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

import static org.breezora.celestial.util.Reflections.*;

/**
 * Part of a scoreboard represents the reflective base of it
 *
 * @since 1.0.0
 */
public abstract class ReflectiveScoreboardBase {

    @SuppressWarnings("deprecation")
    protected static final String[] LEGACY_COLOR_CODES = Arrays.stream(ChatColor.values()) // Stupid legacy shit because the adventure color is rendered as a text
            .map(Object::toString)
            .toArray(String[]::new);
    private static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(8);

    private static final Class<?> CHAT_COMPONENT_CLAZZ; // Packets and their components
    private static final Class<?> CHAT_FORMAT_ENUM;
    private static final Object RESET_FORMATTING;
    private static final MethodHandle PLAYER_CONNECTION;
    private static final MethodHandle PLAYER_GET_HANDLE;
    private static final MethodHandle SEND_PACKET;
    private static final MethodHandle FIXED_NUMBER_FORMAT;

    private static final Reflections.PacketConstructor PACKET_SCOREBOARD_OBJECTIVE; // Scoreboard packets
    private static final Reflections.PacketConstructor PACKET_SCOREBOARD_DISPLAY_OBJECTIVE;
    private static final Reflections.PacketConstructor PACKET_SCOREBOARD_TEAM;
    private static final Reflections.PacketConstructor PACKET_SCOREBOARD_SERIALIZABLE_TEAM;
    private static final MethodHandle PACKET_SCOREBOARD_SET_SCORE;
    private static final MethodHandle PACKET_SCOREBOARD_RESET_SCORE;
    private static final boolean SCORE_OPTIONAL_COMPONENTS;

    private static final Class<?> DISPLAY_SLOT_TYPE; // Scoreboard enums
    private static final Class<?> ENUM_SCOREBOARD_HEALTH_DISPLAY;
    private static final Class<?> ENUM_SCOREBOARD_ACTION;
    private static final Object BLANK_NUMBER_FORMAT;
    private static final Object SIDEBAR_DISPLAY_SLOT;
    private static final Object ENUM_SCOREBOARD_HEALTH_DISPLAY_INTEGER;
    private static final Object ENUM_SCOREBOARD_ACTION_CHANGE;
    private static final Object ENUM_SCOREBOARD_ACTION_REMOVE;

    private static final Object EMPTY_COMPONENT; // Paper specific
    private static final MethodHandle COMPONENT_METHOD;

    private final Player player;
    private boolean deleted = false;

    static {
        try {
            var lookup = MethodHandles.lookup();

            var craftPlayerClazz = clazz("org.bukkit.craftbukkit.entity.CraftPlayer");
            var entityPlayerClazz = clazz("net.minecraft.server.level.ServerPlayer");
            var playerConnectionClazz = clazz("net.minecraft.server.network.ServerGamePacketListenerImpl");
            var packetClazz = clazz("net.minecraft.network.protocol.Packet");
            var packetScoreboardObjectiveClazz = clazz("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket");
            var packetScoreboardDisplayObjectiveClazz = clazz("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket");
            var packetScoreboardScoreClazz = clazz("net.minecraft.network.protocol.game.ClientboundSetScorePacket");
            var packetScoreboardTeamClazz = clazz("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket");
            var scoreboardTeamClazz = innerClazz(packetScoreboardTeamClazz, inner -> !inner.isEnum());
            var playerConnectionField = Arrays.stream(entityPlayerClazz.getFields())
                    .filter(field -> field.getType().isAssignableFrom(playerConnectionClazz))
                    .findFirst().orElseThrow(NoSuchFieldException::new);
            var sendPacketMethod = Stream.concat(Arrays.stream(playerConnectionClazz.getSuperclass().getMethods()), Arrays.stream(playerConnectionClazz.getMethods()))
                    .filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0] == packetClazz)
                    .findFirst().orElseThrow(NoSuchMethodException::new);
            var displaySlotEnum = optionalClazz("net.minecraft.world.scores.DisplaySlot");

            CHAT_COMPONENT_CLAZZ = clazz("net.minecraft.network.chat.Component");
            CHAT_FORMAT_ENUM = clazz("net.minecraft.ChatFormatting");
            DISPLAY_SLOT_TYPE = displaySlotEnum.orElse(int.class);
            RESET_FORMATTING = enumValue(CHAT_FORMAT_ENUM, "RESET", 21);
            SIDEBAR_DISPLAY_SLOT = displaySlotEnum.isPresent() ? enumValue(DISPLAY_SLOT_TYPE, "SIDEBAR", 1) : 1;
            PLAYER_GET_HANDLE = lookup.findVirtual(craftPlayerClazz, "getHandle", MethodType.methodType(entityPlayerClazz));
            PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField);
            SEND_PACKET = lookup.unreflect(sendPacketMethod);
            PACKET_SCOREBOARD_OBJECTIVE = findPacketConstructor(packetScoreboardObjectiveClazz, lookup);
            PACKET_SCOREBOARD_DISPLAY_OBJECTIVE = findPacketConstructor(packetScoreboardDisplayObjectiveClazz, lookup);

            var numberFormat = optionalClazz("net.minecraft.network.chat.numbers.NumberFormat");
            var blankFormatClazz = clazz("net.minecraft.network.chat.numbers.BlankFormat");
            var fixedFormatClazz = clazz("net.minecraft.network.chat.numbers.FixedFormat");
            var resetScoreClazz = clazz("net.minecraft.network.protocol.game.ClientboundResetScorePacket");
            var scoreType = MethodType.methodType(void.class, String.class, String.class, int.class, CHAT_COMPONENT_CLAZZ, numberFormat.get());
            var scoreTypeOptional = MethodType.methodType(void.class, String.class, String.class, int.class, Optional.class, Optional.class);
            var removeScoreType = MethodType.methodType(void.class, String.class, String.class);
            var fixedFormatType = MethodType.methodType(void.class, CHAT_COMPONENT_CLAZZ);
            var blankField = Arrays.stream(blankFormatClazz.getFields()).filter(field -> field.getType() == blankFormatClazz).findAny();
            var optionalScorePacket = optionalConstructor(packetScoreboardScoreClazz, lookup, scoreTypeOptional);

            PACKET_SCOREBOARD_SET_SCORE = optionalScorePacket.isPresent() ? optionalScorePacket.get() : lookup.findConstructor(packetScoreboardScoreClazz, scoreType);
            PACKET_SCOREBOARD_RESET_SCORE = lookup.findConstructor(resetScoreClazz, removeScoreType);
            PACKET_SCOREBOARD_TEAM = findPacketConstructor(packetScoreboardTeamClazz, lookup);
            PACKET_SCOREBOARD_SERIALIZABLE_TEAM = scoreboardTeamClazz == null ? null : findPacketConstructor(scoreboardTeamClazz, lookup);
            FIXED_NUMBER_FORMAT = lookup.findConstructor(fixedFormatClazz, fixedFormatType);
            BLANK_NUMBER_FORMAT = blankField.isPresent() ? blankField.get().get(null) : null;
            SCORE_OPTIONAL_COMPONENTS = optionalScorePacket.isPresent();

            for (var clazz : Arrays.asList(packetScoreboardObjectiveClazz, packetScoreboardDisplayObjectiveClazz, packetScoreboardScoreClazz, packetScoreboardTeamClazz, scoreboardTeamClazz)) {
                if (clazz == null) continue;

                var fields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toArray(Field[]::new);
                for (var field : fields) {
                    field.setAccessible(true);
                }
                PACKETS.put(clazz, fields);
            }

            ENUM_SCOREBOARD_HEALTH_DISPLAY = clazz("net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType");
            ENUM_SCOREBOARD_ACTION = clazz("net.minecraft.server.ServerScoreboard$Method");
            ENUM_SCOREBOARD_HEALTH_DISPLAY_INTEGER = enumValue(ENUM_SCOREBOARD_HEALTH_DISPLAY, "INTEGER", 0);
            ENUM_SCOREBOARD_ACTION_CHANGE = enumValue(ENUM_SCOREBOARD_ACTION, "CHANGE", 0);
            ENUM_SCOREBOARD_ACTION_REMOVE = enumValue(ENUM_SCOREBOARD_ACTION, "REMOVE", 1);

            var paperAdventure = Class.forName("io.papermc.paper.adventure.PaperAdventure");
            var method = paperAdventure.getDeclaredMethod("asVanilla", Component.class);

            COMPONENT_METHOD = lookup.unreflect(method);
            EMPTY_COMPONENT = COMPONENT_METHOD.invoke(Component.empty());

        } catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }

    protected ReflectiveScoreboardBase(Player player) {
        this.player = player;
    }

    protected Component lineByScore(List<Component> lines, int score) {
        return score < lines.size() ? lines.get(lines.size() - score - 1) : null;
    }

    protected void sendObjectivePacket(Lifecycle.Objective lifecycle, String uniqueName, Component title) throws Throwable {
        var packet = PACKET_SCOREBOARD_OBJECTIVE.invoke();
        set(packet, String.class, uniqueName);
        set(packet, int.class, lifecycle.ordinal());

        if (lifecycle != Lifecycle.Objective.REMOVE) {
            setComponent(packet, title, 1);
            set(packet, Optional.class, Optional.empty()); // Number format for 1.20.5+
            set(packet, ENUM_SCOREBOARD_HEALTH_DISPLAY, ENUM_SCOREBOARD_HEALTH_DISPLAY_INTEGER);
        }
        sendPacket(packet);
    }

    protected void sendDisplayObjectivePacket(String uniqueName) throws Throwable {
        var packet = PACKET_SCOREBOARD_DISPLAY_OBJECTIVE.invoke();
        set(packet, DISPLAY_SLOT_TYPE, SIDEBAR_DISPLAY_SLOT); // Position - maybe has to be modified to support the below name scoreboard
        set(packet, String.class, uniqueName); // Score name

        sendPacket(packet);
    }

    protected void sendScorePacket(String uniqueName, List<Component> scores, int score, Lifecycle.Scoreboard lifecycle) throws Throwable {
        var objectName = LEGACY_COLOR_CODES[score];
        var enumAction = lifecycle == Lifecycle.Scoreboard.REMOVE ? ENUM_SCOREBOARD_ACTION_REMOVE : ENUM_SCOREBOARD_ACTION_CHANGE;

        if (PACKET_SCOREBOARD_RESET_SCORE == null) {
            sendPacket(PACKET_SCOREBOARD_SET_SCORE.invoke(enumAction, uniqueName, objectName, score));
            return;
        }

        if (lifecycle == Lifecycle.Scoreboard.REMOVE) {
            sendPacket(PACKET_SCOREBOARD_RESET_SCORE.invoke(objectName, uniqueName));
            return;
        }

        var scoreFormat = lineByScore(scores, score);
        var format = scoreFormat != null
                ? FIXED_NUMBER_FORMAT.invoke(asMinecraftComponent(scoreFormat))
                : BLANK_NUMBER_FORMAT;
        var packet = SCORE_OPTIONAL_COMPONENTS
                ? PACKET_SCOREBOARD_SET_SCORE.invoke(objectName, uniqueName, score, Optional.empty(), Optional.of(format))
                : PACKET_SCOREBOARD_SET_SCORE.invoke(objectName, uniqueName, score, null, format);

        sendPacket(packet);
    }

    protected void sendTeamPacket(String uniqueName, int score, Lifecycle.Team lifecycle, Component prefix, Component suffix) throws Throwable {
        if (lifecycle == Lifecycle.Team.ADD_PLAYERS || lifecycle == Lifecycle.Team.REMOVE_PLAYERS) throw new UnsupportedOperationException();

        var packet = PACKET_SCOREBOARD_TEAM.invoke();
        set(packet, String.class, uniqueName + ":" + score); // Team name
        set(packet, int.class, lifecycle.ordinal(), 0); // Update lifecycle

        if (lifecycle == Lifecycle.Team.REMOVE) {
            sendPacket(packet);
            return;
        }

        var team = PACKET_SCOREBOARD_SERIALIZABLE_TEAM.invoke();
        setComponent(team, null, 0); // Display name
        set(team, CHAT_FORMAT_ENUM, RESET_FORMATTING); // Color
        setComponent(team, prefix, 1); // Prefix
        setComponent(team, suffix, 2); // Suffix
        set(team, String.class, "always", 0); // Visibility
        set(team, String.class, "always", 1); // Collisions
        set(packet, Optional.class, Optional.of(team));

        if (lifecycle == Lifecycle.Team.CREATE) {
            set(packet, Collection.class, Collections.singletonList(LEGACY_COLOR_CODES[score])); // Players on that team
        }
        sendPacket(packet);
    }

    protected void sendTeamPacket(String uniqueName, int score, Lifecycle.Team lifecycle) throws Throwable {
        sendTeamPacket(uniqueName, score, lifecycle, null, null);
    }

    private void sendPacket(Object packet) throws Throwable {
        if (deleted) throw new IllegalStateException("Cannot send packet after the scoreboard is deleted");
        if (!player.isOnline()) return;

        var entityPlayer = PLAYER_GET_HANDLE.invoke(player);
        var playerConnection = PLAYER_CONNECTION.invoke(entityPlayer);
        SEND_PACKET.invoke(playerConnection, packet);
    }

    private void set(Object object, Class<?> fieldType, Object value) throws ReflectiveOperationException {
        set(object, fieldType, value, 0);
    }

    private void set(Object packet, Class<?> fieldType, Object value, int count) throws ReflectiveOperationException {
        var i = 0;
        for (var field : PACKETS.get(packet.getClass())) {
            if (field.getType() == fieldType && count == i++) {
                field.set(packet, value);
            }
        }
    }

    private void setComponent(Object packet, Component value, int count) throws Throwable {
        var i = 0;
        for (var field : PACKETS.get(packet.getClass())) {
            if ((field.getType() == String.class || field.getType() == CHAT_COMPONENT_CLAZZ) && count == i++) {
                field.set(packet, asMinecraftComponent(value));
            }
        }
    }

    private Object asMinecraftComponent(Component component) throws Throwable {
        if (component == null) return EMPTY_COMPONENT;
        return COMPONENT_METHOD.invoke(component);
    }

}
