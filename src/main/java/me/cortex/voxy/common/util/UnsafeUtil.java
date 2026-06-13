package me.cortex.voxy.common.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {
    private static final Unsafe UNSAFE;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe)field.get(null);
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    private static final long BYTE_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
    private static final long LONG_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(long[].class);

    public static void memcpy(long src, long dst, long length) {
        UNSAFE.copyMemory(src, dst, length);
    }

    public static void memcpy(long src, int length, byte[] dst, int offset) {
        UNSAFE.copyMemory(null, src, dst, BYTE_ARRAY_BASE_OFFSET+offset, length);
    }

    public static void memcpy(long[] src, long dst) {
        UNSAFE.copyMemory(src, LONG_ARRAY_BASE_OFFSET, null, dst, (long) src.length <<3);
    }

    //Cause lwjgl is being a clown with var handles, we need to do things ourselves
    public static void memPutInt(long ptr, int value)       { UNSAFE.putInt(null, ptr, value); }

}
