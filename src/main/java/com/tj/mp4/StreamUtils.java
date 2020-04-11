package com.tj.mp4;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;

public class StreamUtils {
    public static String readString(RandomAccessFile stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (char c = stream.readChar(); c != 0; ) {
            builder.append(c);
        }
        return builder.toString();
    }

    public static String readWord(RandomAccessFile stream, int wordSize) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < wordSize; i++) {
            builder.append((char) stream.read());
        }
        return builder.toString();
    }

    public static <T> T[] readArray(RandomAccessFile stream, Class<T> type, int length) throws IOException {

        if (type == int.class || type == byte.class || type == long.class
                || type == double.class || type == float.class || type == char.class || type == short.class) {
            throw new IllegalArgumentException();
        }
        @SuppressWarnings("unchecked")
        T[] ret = (T[]) Array.newInstance(type, length);
        for (int i = 0; i < length; i++) {
            if (type == int.class || type == Integer.class) {
                ret[i] = type.cast(stream.readInt());
            } else if (type == byte.class || type == Byte.class) {
                ret[i] = type.cast(stream.readByte());
            } else if (type == long.class || type == Long.class) {
                ret[i] = type.cast(stream.readLong());
            } else if (type == double.class || type == Double.class) {
                ret[i] = type.cast(stream.readDouble());
            } else if (type == float.class || type == Float.class) {
                ret[i] = type.cast(stream.readFloat());
            } else if (type == char.class || type == Character.class) {
                ret[i] = type.cast(stream.readChar());
            } else if (type == short.class || type == Short.class) {
                ret[i] = type.cast(stream.readShort());
            } else {
                throw new UnsupportedOperationException("Type not supported:" + type.getTypeName());
            }
        }
        return ret;
    }
}
