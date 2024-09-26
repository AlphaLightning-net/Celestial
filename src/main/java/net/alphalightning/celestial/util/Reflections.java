package net.alphalightning.celestial.util;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.function.Predicate;

public final class Reflections {

    private static final MethodType VOID_TYPE = MethodType.methodType(void.class);
    private static volatile Object theUnsafe;

    private Reflections() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static Class<?> clazz(@NotNull String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    public static Object enumValue(Class<?> clazz, String constant) {
        return Enum.valueOf(clazz.asSubclass(Enum.class), constant);
    }

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

    public static Class<?> innerClazz(Class<?> parent, Predicate<Class<?>> predicate) throws ClassNotFoundException {
        for (var inner : parent.getDeclaredClasses()) {
            if (predicate.test(inner)) {
                return inner;
            }
        }
        throw new ClassNotFoundException("No class in " + parent.getCanonicalName() + " matches the predicate");
    }

    public static Optional<Class<?>> optionalClazz(String clazzName) {
        try {
            return Optional.of(clazz(clazzName));

        } catch (ClassNotFoundException exception) {
            return Optional.empty();
        }
    }

    public static Optional<MethodHandle> optionalConstructor(Class<?> clazz, MethodHandles.Lookup lookup, MethodType type) throws IllegalAccessException {
        try {
            return Optional.of(lookup.findConstructor(clazz, type));
        } catch (NoSuchMethodException exception) {
            return Optional.empty();
        }
    }

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

    @FunctionalInterface
    public interface PacketConstructor {
        Object invoke() throws Throwable;
    }

}
