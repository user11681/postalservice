package user11681.postalservice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import user11681.postalservice.test.TestPacket;

public class ThePostalService {
    private static final MethodHandles.Lookup lookup;
    private static final MethodHandle putInt;
    private static final MethodHandle getObject;
    private static final MethodHandle putBoolean;
    private static final MethodHandle staticFieldOffset;
    private static final MethodHandle objectFieldOffset;
    private static final MethodHandle allocateInstance;
    private static final MethodHandle getDeclaredFields0;
    private static final long modifiersOffset;
    private static final long overrideOffset;

    private static final Charset charset = StandardCharsets.UTF_8;
    private static final Logger logger = LogManager.getLogger("The Postal Service");
    private static final Reference2ReferenceOpenHashMap<Class<?>, Object2ReferenceOpenHashMap<String, Field>> fieldCache = new Reference2ReferenceOpenHashMap<>();
    private static final Int2ReferenceOpenHashMap<Object> readObjects = new Int2ReferenceOpenHashMap<>();
    private static final Reference2IntOpenHashMap<Object> writtenObjects = new Reference2IntOpenHashMap<>();
    private static final Object2IntOpenHashMap<String> writtenStrings = new Object2IntOpenHashMap<>();

    private static int readObjectCount;
    private static int writtenObjectCount;
    private static int writtenStringCount;

    private static final int NULL = 0;
    private static final int INT = 1;
    private static final int LONG = 2;
    private static final int FLOAT = 3;
    private static final int DOUBLE = 4;
    private static final int BYTE = 5;
    private static final int CHAR = 6;
    private static final int SHORT = 7;
    private static final int BOOLEAN = 8;
    private static final int REFERENCE = 9;
    private static final int STRING = 10;
    private static final int CLASS = 11;
    private static final int ENUM = 12;
    private static final int ARRAY = 13;
    private static final int OBJECT = 14;

    public static synchronized <T> T readObject(final ByteBuf buffer) {
        final T object = readObjectUnsynchonized(buffer);

        readObjects.clear();
        readObjectCount = 0;

        return object;
    }

    public static PacketByteBuf writeObject(final Object object) {
        return writeObject(new PacketByteBuf(Unpooled.buffer()), object);
    }

    public static synchronized <T extends ByteBuf> T writeObject(final T buffer, final Object object) {
        writeObject(buffer, object, object == null ? null : object.getClass());

        writtenObjects.clear();
        writtenObjectCount = 0;

        return buffer;
    }

    private static <T> T readObjectUnsynchonized(final ByteBuf buffer) {
        try {
            final int type = buffer.readByte();

            switch (type) {
                case NULL:
                    return null;
                case INT:
                    return (T) (Integer) buffer.readInt();
                case LONG:
                    return (T) (Long) buffer.readLong();
                case FLOAT:
                    return (T) (Float) buffer.readFloat();
                case DOUBLE:
                    return (T) (Double) buffer.readDouble();
                case BYTE:
                    return (T) (Byte) buffer.readByte();
                case CHAR:
                    return (T) (Character) buffer.readChar();
                case SHORT:
                    return (T) (Short) buffer.readShort();
                case BOOLEAN:
                    return (T) (Boolean) buffer.readBoolean();
                case REFERENCE:
                    return (T) readObjects.get(buffer.readInt());
                default:
                    final T object;

                    switch (type) {
                        case STRING:
                            return (T) buffer.readCharSequence(buffer.readInt(), charset).toString();
                        case CLASS:
                            return (T) Class.forName(buffer.readCharSequence(buffer.readInt(), charset).toString());
                        case ENUM:
                            Class<?> enumeration = Class.forName(buffer.readCharSequence(buffer.readInt(), charset).toString());

                            if (enumeration.isAnonymousClass()) {
                                enumeration = enumeration.getSuperclass();
                            }

                            // This cast prevents the compiler from throwing an error.
                            //noinspection RedundantCast
                            object = (T) getEnumConstant(enumeration, buffer.readCharSequence(buffer.readInt(), charset).toString());
                            readObjects.put(readObjectCount++, object);

                            return object;
                        case ARRAY:
                            final int arrayType = buffer.readByte();
                            final int length = buffer.readInt();
                            int i;

                            switch (arrayType) {
                                case INT:
                                    final int[] intArray = new int[length];

                                    for (i = 0; i != length; i++) {
                                        intArray[i] = buffer.readInt();
                                    }

                                    readObjects.put(readObjectCount++, intArray);

                                    return (T) intArray;
                                case LONG:
                                    final long[] longArray = new long[length];

                                    for (i = 0; i != length; i++) {
                                        longArray[i] = buffer.readLong();
                                    }

                                    readObjects.put(readObjectCount++, longArray);

                                    return (T) longArray;
                                case FLOAT:
                                    final float[] floatArray = new float[length];

                                    for (i = 0; i != length; i++) {
                                        floatArray[i] = buffer.readFloat();
                                    }

                                    readObjects.put(readObjectCount++, floatArray);

                                    return (T) floatArray;
                                case DOUBLE:
                                    final double[] doubleArray = new double[length];

                                    for (i = 0; i != length; i++) {
                                        doubleArray[i] = buffer.readDouble();
                                    }

                                    readObjects.put(readObjectCount++, doubleArray);

                                    return (T) doubleArray;
                                case BYTE:
                                    final byte[] byteArray = new byte[length];

                                    for (i = 0; i != length; i++) {
                                        byteArray[i] = buffer.readByte();
                                    }

                                    readObjects.put(readObjectCount++, byteArray);

                                    return (T) byteArray;
                                case CHAR:
                                    final char[] charArray = new char[length];

                                    for (i = 0; i != length; i++) {
                                        charArray[i] = buffer.readChar();
                                    }

                                    readObjects.put(readObjectCount++, charArray);

                                    return (T) charArray;
                                case SHORT:
                                    final short[] shortArray = new short[length];

                                    for (i = 0; i != length; i++) {
                                        shortArray[i] = buffer.readShort();
                                    }

                                    readObjects.put(readObjectCount++, shortArray);

                                    return (T) shortArray;
                                case BOOLEAN:
                                    final boolean[] booleanArray = new boolean[length];

                                    for (i = 0; i != length; i++) {
                                        booleanArray[i] = buffer.readBoolean();
                                    }

                                    readObjects.put(readObjectCount++, booleanArray);

                                    return (T) booleanArray;
                                default:
                                    final String componentTypeName = buffer.readCharSequence(buffer.readInt(), charset).toString();
                                    final Object[] array = (Object[]) Array.newInstance(Class.forName(componentTypeName), length);

                                    for (i = 0; i != length; i++) {
                                        array[i] = readObjectUnsynchonized(buffer);
                                    }

                                    readObjects.put(readObjectCount++, array);

                                    return (T) array;
                            }
                        case OBJECT:
                            String className = buffer.readCharSequence(buffer.readInt(), charset).toString();
                            Class<?> klass = Class.forName(className);
                            Object2ReferenceOpenHashMap<String, Field> classFields = fieldCache.get(klass);
                            readObjects.put(readObjectCount++, object = (T) allocateInstance.invokeExact(klass));

                            if (classFields == null) {
                                cache(klass);
                            }

                            int fieldCount;
                            int j;

                            while (klass != Object.class) {
                                classFields = fieldCache.get(klass);
                                fieldCount = buffer.readInt();

                                for (j = 0; j != fieldCount; j++) {
                                    classFields.get(buffer.readCharSequence(buffer.readInt(), charset).toString()).set(object, readObjectUnsynchonized(buffer));
                                }

                                klass = klass.getSuperclass();
                            }

                            return object;
                        default:
                            return null;
                    }
            }
        } catch (final Throwable throwable) {
            logger.error("An error was encountered while attempting to read an object from a buffer:", throwable);
        }

        return null;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private static void writeObject(final ByteBuf buffer, final Object object, Class<?> klass) {
        if (object == null) {
            buffer.writeInt(NULL);
        } else if (klass == int.class) {
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
            buffer.writeByte(CHAR);
            buffer.writeChar((char) object);
        } else if (klass == short.class) {
            buffer.writeByte(SHORT);
            buffer.writeShort((short) object);
        } else if (klass == boolean.class) {
            buffer.writeByte(BOOLEAN);
            buffer.writeBoolean((boolean) object);
        } else if (writtenObjects.containsKey(object)) {
            buffer.writeByte(REFERENCE);
            buffer.writeInt(writtenObjects.getInt(object));
        } else {
            if (klass == String.class) {
                if (writtenStrings.containsKey(object)) {
                    buffer.writeByte(REFERENCE);
                    buffer.writeInt(writtenStrings.getInt(object));
                } else {
                    final String string = (String) object;

                    buffer.writeByte(STRING);
                    buffer.writeInt(string.length());
                    buffer.writeCharSequence(string, charset);

                    writtenStrings.put(string, writtenStringCount++);
                }
            } else {
                writtenObjects.put(object, writtenObjectCount++);

                if (object instanceof Class) {
                    final String name = ((Class<?>) object).getName();

                    buffer.writeByte(CLASS);
                    buffer.writeInt(name.length());
                    buffer.writeCharSequence(name, charset);
                } else if (object instanceof Enum) {
                    final String className = klass.getName();
                    final String constantName = ((Enum<?>) object).name();

                    buffer.writeByte(ENUM);
                    buffer.writeInt(className.length());
                    buffer.writeCharSequence(className, charset);
                    buffer.writeInt(constantName.length());
                    buffer.writeCharSequence(constantName, charset);
                } else if (klass.isArray()) {
                    buffer.writeByte(ARRAY);

                    final Class<?> componentType = klass.getComponentType();
                    int length;
                    int i;

                    if (componentType == int.class) {
                        buffer.writeByte(INT);

                        final int[] intArray = (int[]) object;

                        length = intArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeInt(intArray[i]);
                        }
                    } else if (componentType == long.class) {
                        buffer.writeByte(LONG);

                        final long[] longArray = (long[]) object;

                        length = longArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeLong(longArray[i]);
                        }
                    } else if (componentType == float.class) {
                        buffer.writeByte(FLOAT);

                        final float[] floatArray = (float[]) object;

                        length = floatArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeFloat(floatArray[i]);
                        }
                    } else if (componentType == double.class) {
                        buffer.writeByte(DOUBLE);

                        final double[] doubleArray = (double[]) object;

                        length = doubleArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeDouble(doubleArray[i]);
                        }
                    } else if (componentType == byte.class) {
                        buffer.writeByte(BYTE);

                        final byte[] byteArray = (byte[]) object;

                        length = byteArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeByte(byteArray[i]);
                        }
                    } else if (componentType == char.class) {
                        buffer.writeByte(CHAR);

                        final char[] charArray = (char[]) object;

                        length = charArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeChar(charArray[i]);
                        }
                    } else if (componentType == short.class) {
                        buffer.writeByte(SHORT);

                        final short[] shortArray = (short[]) object;

                        length = shortArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeShort(shortArray[i]);
                        }
                    } else if (componentType == boolean.class) {
                        buffer.writeByte(BOOLEAN);

                        final boolean[] booleanArray = (boolean[]) object;

                        length = booleanArray.length;
                        buffer.writeInt(length);

                        for (i = 0; i != length; i++) {
                            buffer.writeBoolean(booleanArray[i]);
                        }
                    } else {
                        buffer.writeByte(OBJECT);

                        final Object[] array = (Object[]) object;

                        length = array.length;
                        buffer.writeInt(length);

                        final String className = componentType.getName();

                        buffer.writeInt(className.length());
                        buffer.writeCharSequence(className, charset);

                        Object element;

                        for (i = 0; i != length; i++) {
                            writeObject(buffer, element = array[i], element == null ? null : element.getClass());
                        }
                    }
                } else {
                    try {
                        buffer.writeByte(OBJECT);

                        final String className = klass.getName();
                        Object2ReferenceOpenHashMap<String, Field> classFields = fieldCache.get(klass);

                        if (classFields == null) {
                            cache(klass);
                        }

                        buffer.writeInt(className.length());
                        buffer.writeCharSequence(className, charset);

                        Field field;
                        Class<?> fieldType;
                        String fieldName;
                        Object fieldValue;
                        int fieldCount;

                        while (klass != Object.class) {
                            classFields = fieldCache.get(klass);
                            fieldCount = classFields.size();

                            buffer.writeInt(fieldCount);

                            for (final Map.Entry<String, Field> fieldEntry : classFields.object2ReferenceEntrySet()) {
                                field = fieldEntry.getValue();
                                fieldType = field.getType();
                                fieldName = field.getName();

                                buffer.writeInt(fieldName.length());
                                buffer.writeCharSequence(fieldName, charset);

                                fieldValue = field.get(object);

                                writeObject(buffer, fieldValue, fieldType.isPrimitive() ? fieldType : fieldValue == null ? null : fieldValue.getClass());
                            }

                            klass = klass.getSuperclass();
                        }
                    } catch (final Throwable throwable) {
                        logger.error("An error was encountered while attempting to write an object to a buffer:", throwable);
                    }
                }
            }
        }
    }

    private static void cache(Class<?> klass) {
        try {
            Object2ReferenceOpenHashMap<String, Field> cachedFields;
            Field[] fields;
            Field field;
            int fieldCount;
            int flags;

            while (klass != Object.class) {
                cachedFields = new Object2ReferenceOpenHashMap<>();
                fields = (Field[]) getDeclaredFields0.invokeExact(klass, false);
                fieldCount = fields.length;

                for (int i = 0; i != fieldCount; i++) {
                    field = fields[i];

                    if (((flags = field.getModifiers()) & Opcodes.ACC_STATIC) == 0) {
                        putBoolean.invokeExact((Object) field, overrideOffset, true);

                        if ((flags & Opcodes.ACC_FINAL) != 0) {
                            putInt.invokeExact((Object) field, modifiersOffset, flags & ~Opcodes.ACC_FINAL);
                        }

                        cachedFields.put(field.getName(), field);
                    }
                }

                fieldCache.put(klass, cachedFields);
                klass = klass.getSuperclass();
            }
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private static <T extends Enum<T>> T getEnumConstant(final Class<?> klass, final String name) {
        return Enum.valueOf((Class<T>) klass, name);
    }

    static {
        ServerSidePacketRegistry.INSTANCE.register(TestPacket.identifier, TestPacket.instance);
        ClientSidePacketRegistry.INSTANCE.register(TestPacket.identifier, TestPacket.instance);

        try {
            final Class<?> PlatformDependent0 = Class.forName("io.netty.util.internal.PlatformDependent0");
            final Field INTERNAL_UNSAFE = PlatformDependent0.getDeclaredField("INTERNAL_UNSAFE");
            Object unsafe;

            INTERNAL_UNSAFE.setAccessible(true);
            unsafe = INTERNAL_UNSAFE.get(null);

            if (unsafe == null) {
                final Field UNSAFE = PlatformDependent0.getDeclaredField("UNSAFE");
                UNSAFE.setAccessible(true);
                unsafe = UNSAFE.get(null);
            }

            final MethodHandles.Lookup temporaryLookup = MethodHandles.lookup();

            putInt = temporaryLookup.bind(unsafe, "putInt", MethodType.methodType(void.class, Object.class, long.class, int.class));
            getObject = temporaryLookup.bind(unsafe, "getObject", MethodType.methodType(Object.class, Object.class, long.class));
            putBoolean = temporaryLookup.bind(unsafe, "putBoolean", MethodType.methodType(void.class, Object.class, long.class, boolean.class));
            staticFieldOffset = temporaryLookup.bind(unsafe, "staticFieldOffset", MethodType.methodType(long.class, Field.class));
            objectFieldOffset = temporaryLookup.bind(unsafe, "objectFieldOffset", MethodType.methodType(long.class, Field.class));
            allocateInstance = temporaryLookup.bind(unsafe, "allocateInstance", MethodType.methodType(Object.class, Class.class));

            final Field IMPL_LOOKUP = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup = (MethodHandles.Lookup) (Object) getObject.invokeExact((Object) MethodHandles.Lookup.class, (long) staticFieldOffset.invokeExact(IMPL_LOOKUP));

            getDeclaredFields0 = lookup.findVirtual(Class.class, "getDeclaredFields0", MethodType.methodType(Field[].class, boolean.class));
            long modifiersFieldOffset = -1;
            long overrideFieldOffset = -1;

            for (final Field field : (Field[]) getDeclaredFields0.invokeExact(AccessibleObject.class, false)) {
                if ("override".equals(field.getName())) {
                    overrideFieldOffset = (long) objectFieldOffset.invokeExact(field);

                    break;
                }
            }

            for (final Field field : (Field[]) getDeclaredFields0.invokeExact(Field.class, false)) {
                if ("modifiers".equals(field.getName())) {
                    modifiersFieldOffset = (long) objectFieldOffset.invokeExact(field);

                    break;
                }
            }

            modifiersOffset = modifiersFieldOffset;
            overrideOffset = overrideFieldOffset;
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
