package user11681.postalservice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.nio.cs.UTF_8;

public class ThePostalService {
    public static final Object unsafe;
    public static final Class<?> unsafeClass;

    private static final MethodHandles.Lookup lookup;
    private static final MethodHandle allocateInstance;
    private static final UTF_8 charset = UTF_8.INSTANCE;

    private static final Logger logger = LogManager.getLogger("The Postal Service");
    private static final Object2ReferenceOpenHashMap<String, Object2ReferenceOpenHashMap<String, Field>> fieldCache = new Object2ReferenceOpenHashMap<>();
    private static final Reference2IntOpenHashMap<Class<?>> assignableClassCounts = new Reference2IntOpenHashMap<>();

    private static final int BYTE = 0;
    private static final int SHORT = 1;
    private static final int INT = 2;
    private static final int LONG = 3;
    private static final int FLOAT = 4;
    private static final int DOUBLE = 5;
    private static final int CHARACTER = 6;
    private static final int STRING = 7;
    private static final int ARRAY = 8;
    private static final int OBJECT = 9;

    public static void init() {}

    public static PacketByteBuf write(final Object object) {
        return write(new PacketByteBuf(Unpooled.buffer()), object);
    }

    public static <T extends ByteBuf> T write(final T buffer, final Object object) {
        write(buffer, object, object.getClass());

        return buffer;
    }

    private static void write(final ByteBuf buffer, final Object object, Class<?> klass) {
        if (klass == int.class) {
            buffer.writeByte(INT);
            buffer.writeInt((int) object);
        } else if (klass == long.class) {
            buffer.writeByte(LONG);
            buffer.writeLong((long) object);
        } else if (klass == float.class) {
            buffer.writeByte(FLOAT);
            buffer.writeFloat((float) object);
        } else if (klass == double.class) {
            buffer.writeByte(DOUBLE);
            buffer.writeDouble((double) object);
        } else if (klass == byte.class) {
            buffer.writeByte(BYTE);
            buffer.writeByte((byte) object);
        } else if (klass == char.class) {
            buffer.writeByte(CHARACTER);
            buffer.writeChar((int) object);
        } else if (klass == short.class) {
            buffer.writeByte(SHORT);
            buffer.writeShort((short) object);
        } else if (klass == String.class) {
            buffer.writeByte(STRING);
            buffer.writeInt(((String) object).length());
            buffer.writeCharSequence((String) object, charset);
        } else if (klass.isArray()) {
            buffer.writeByte(ARRAY);

            final Class<?> componentType = klass.getComponentType();
            int length;
            int i;

            if (object instanceof Object[]) {
                final Object[] objectArray = (Object[]) object;

                length = objectArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    write(buffer, objectArray[i]);
                }
            } else if (componentType == int.class) {
                final int[] intArray = (int[]) object;

                length = intArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    buffer.writeInt(intArray[i]);
                }
            } else if (componentType == long.class) {
                final long[] longArray = (long[]) object;

                length = longArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    buffer.writeLong(longArray[i]);
                }
            } else if (componentType == float.class) {
                final float[] floatArray = (float[]) object;

                length = floatArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    buffer.writeFloat(floatArray[i]);
                }
            } else if (componentType == double.class) {
                final double[] doubleArray = (double[]) object;

                length = doubleArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    buffer.writeDouble(doubleArray[i]);
                }
            } else if (componentType == byte.class) {
                final byte[] byteArray = (byte[]) object;

                length = byteArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    buffer.writeByte(byteArray[i]);
                }
            } else if (componentType == char.class) {
                final char[] charArray = (char[]) object;

                length = charArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    buffer.writeChar(charArray[i]);
                }
            } else if (componentType == short.class) {
                final short[] shortArray = (short[]) object;

                length = shortArray.length;
                buffer.writeInt(length);

                for (i = 0; i != length; i++) {
                    buffer.writeShort(shortArray[i]);
                }
            }
        } else {
            buffer.writeByte(OBJECT);

            try {
                final String className = klass.getName();
                Object2ReferenceOpenHashMap<String, Field> classFields = fieldCache.get(className);

                if (classFields == null) {
                    cache(klass);
                    classFields = fieldCache.get(className);
                }

                buffer.writeInt(assignableClassCounts.getInt(klass));

                Field field;
                int fieldCount;
                int i;

                while (klass != Object.class) {
                    fieldCount = classFields.size();

                    buffer.writeInt(className.length());
                    buffer.writeCharSequence(className, charset);
                    buffer.writeInt(fieldCount);

                    for (final Map.Entry<String, Field> fieldEntry : classFields.object2ReferenceEntrySet()) {
                        field = fieldEntry.getValue();
                        write(buffer, field.get(object), field.getType());
                    }

                    klass = klass.getSuperclass();
                }
            } catch (final Throwable throwable) {
                logger.error("an error was encountered while attempting to write an object to a buffer", throwable);
            }
        }
    }

    public static <T> T parse(final ByteBuf buffer) {
        try {
            final int classNameLength = buffer.readInt();
            final String className = buffer.readCharSequence(classNameLength, charset).toString();
            Class<?> klass = Class.forName(className);
            final T object = (T) allocateInstance.invokeExact(klass);
            Object2ReferenceOpenHashMap<String, Field> classFields = fieldCache.get(className);
            Field field;

            if (classFields == null) {
                cache(klass);
                classFields = fieldCache.get(className);
            }
        } catch (final Throwable throwable) {
            logger.error("an error was encountered while attempting to read an object from a buffer", throwable);
        }

        return null;
    }

    private static void cache(Class<?> klass) {
        Object2ReferenceOpenHashMap<String, Field> cachedFields;
        Field[] fields;
        Field field;
        int fieldCount;
        int assignableCount = 1;

        while (klass != Object.class) {
            ++assignableCount;
            cachedFields = new Object2ReferenceOpenHashMap<>();
            fields = klass.getDeclaredFields();
            fieldCount = fields.length;

            for (int i = 0; i != fieldCount; i++) {
                field = fields[i];
                field.setAccessible(true);

                cachedFields.put(field.getName(), field);
            }

            fieldCache.put(klass.getName(), cachedFields);
            klass = klass.getSuperclass();
        }

        assignableClassCounts.put(klass, assignableCount);
    }

    static {
        try {
            final Class<?> PlatformDependent0 = Class.forName("io.netty.util.internal.PlatformDependent0");
            final Field INTERNAL_UNSAFE = PlatformDependent0.getDeclaredField("INTERNAL_UNSAFE");
            final Field UNSAFE = PlatformDependent0.getDeclaredField("UNSAFE");

            UNSAFE.setAccessible(true);

            final Object intermediaryUnsafe = UNSAFE.get(null);

            if (intermediaryUnsafe == null) {
                INTERNAL_UNSAFE.setAccessible(true);

                unsafe = INTERNAL_UNSAFE.get(null);
            } else {
                unsafe = intermediaryUnsafe;
            }

            unsafeClass = unsafe.getClass();
            lookup = MethodHandles.lookup();
            allocateInstance = lookup.findVirtual(unsafeClass, "allocateInstance", MethodType.methodType(Object.class, Class.class)).bindTo(unsafe);
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
