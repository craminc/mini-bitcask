package org.cramin;

import java.nio.ByteBuffer;

public class Entry {
    public static final int ENTRY_HEAD_LEN = 2 + 4 + 4;
    public static final short DEL = 1;
    public static final short PUT = 2;

    public short mark;
    public int keySize;
    public int valueSize;
    public byte[] key;
    public byte[] value;

    public Entry(short mark, byte[] key, byte[] value) {
        this(mark, key.length, value.length, key, value);
    }

    public Entry(short mark, int keySize, int valueSize, byte[] key, byte[] value) {
        this.mark = mark;
        this.keySize = keySize;
        this.valueSize = valueSize;
        this.key = key;
        this.value = value;
    }

    public Entry(short mark, int keySize, int valueSize) {
        this.mark = mark;
        this.keySize = keySize;
        this.valueSize = valueSize;
    }

    public byte[] encode() {
        ByteBuffer buf = ByteBuffer.allocate(this.size());
        buf.putShort(mark);
        buf.putInt(keySize);
        buf.putInt(valueSize);
        buf.put(key);
        buf.put(value);

        return buf.array();
    }

    public static Entry decode(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        short mark = buf.getShort();
        int keySize = buf.getInt();
        int valueSize = buf.getInt();

        return new Entry(mark, keySize, valueSize);
    }

    public int size() {
        return ENTRY_HEAD_LEN + keySize + valueSize;
    }
}
