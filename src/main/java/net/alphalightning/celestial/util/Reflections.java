package net.alphalightning.celestial.util;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Utility class for handling sketchy reflections
 *
 * @since 1.0.0
 */
public final class Reflections {

    private static final MethodType VOID_TYPE = MethodType.methodType(void.class);
    private static volatile Object theUnsafe;

    private Reflections() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets a class from its name
     *
     * @param name The name
     * @return The class
     * @throws ClassNotFoundException If the class was not found
     */
    public static Class<?> clazz(@NotNull String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    /**
     * Gets an enum value from a class
     *
     * @param clazz    The class
     * @param constant The name of the num value
     * @return The enum value
     */
    public static Object enumValue(Class<?> clazz, String constant) {
        return Enum.valueOf(clazz.asSubclass(Enum.class), constant);
    }

    /**
     * Gets an enum value from a class with a fallback value if the constant was not found
     *
     * @param clazz           The class
     * @param constant        The name of the constant
     * @param fallbackOrdinal The fallback value
     * @return The enum value
     */
    public static Object enumValue(Class<?> clazz, String constant, int fallbackOrdinal) {
        try {
            return enumValue(clazz, constant);
        } catch (IllegalArgumentException exception) {
            var constants = clazz.getEnumConstants();
            if (constants.length > fallbackOrdinal) {
                return constants[fallbackOrdinal];
            }

            throw exception;
        }
    }

    /**
     * Gets a subclass by a filter
     *
     * @param parent    The upper class containing the subclass
     * @param predicate The predicate that works as the filter
     * @return The class
     * @throws ClassNotFoundException If the class was not found
     */
    public static Class<?> innerClazz(Class<?> parent, Predicate<Class<?>> predicate) throws ClassNotFoundException {
        for (var inner : parent.getDeclaredClasses()) {
            if (predicate.test(inner)) {
                return inner;
            }
        }
        throw new ClassNotFoundException("No class in " + parent.getCanonicalName() + " matches the predicate");
    }

    /**
     * Gets a class wrapped in a {@link Optional}
     *
     * @param clazzName The class name
     * @return The optional or an empty optional if the class was not found
     */
    public static Optional<Class<?>> optionalClazz(String clazzName) {
        try {
            return Optional.of(clazz(clazzName));

        } catch (ClassNotFoundException exception) {
            return Optional.empty();
        }
    }

    /**
     * Gets a constructor wrapped in a {@link Optional}
     *
     * @param clazz  The class
     * @param lookup The lookup that finds the constructor
     * @param type   The class type of the constructor
     * @return The optional with the constructor or an empty optional
     * @throws IllegalAccessException If the constructor is not accessible
     */
    public static Optional<MethodHandle> optionalConstructor(Class<?> clazz, MethodHandles.Lookup lookup, MethodType type) throws IllegalAccessException {
        try {
            return Optional.of(lookup.findConstructor(clazz, type));
        } catch (NoSuchMethodException exception) {
            return Optional.empty();
        }
    }

    /**
     * Finds a constructor of a packet by its class
     *
     * @param packet The packet class
     * @param lookup The lookup that finds the constructor
     * @return The packet constructor
     * @throws Exception If the constructor was not found
     */
    public static PacketConstructor findPacketConstructor(Class<?> packet, MethodHandles.Lookup lookup) throws Exception {
        try {
            var constructor = lookup.findConstructor(packet, VOID_TYPE);
            return constructor::invoke;

        } catch (NoSuchMethodException | IllegalAccessException _) {
        }

        if (theUnsafe == null) {
            synchronized (Reflections.class) {
                if (theUnsafe == null) {
                    var unsafeClazz = Class.forName("sun.misc.Unsafe");
                    var theUnsafeField = unsafeClazz.getDeclaredField("theUnsafe");
                    theUnsafeField.setAccessible(true);
                    theUnsafe = theUnsafeField.get(null);
                }
            }
        }

        var allocateMethodType = MethodType.methodType(Object.class, Class.class);
        var allocateMethod = lookup.findVirtual(theUnsafe.getClass(), "allocateInstance", allocateMethodType);
        return () -> allocateMethod.invoke(theUnsafe, packet);
    }

    /**
     * Represents a constructor of a packet
     *
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface PacketConstructor {

        /**
         * Calls the constructor
         *
         * @return The created packet
         * @throws Throwable If something went wrong
         */
        Object invoke() throws Throwable;
    }

}
