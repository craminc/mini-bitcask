package org.cramin;

import java.nio.ByteBuffer;

public class Entry {
    private static final int ENTRY_HEAD_LEN = 2 + 4 + 4;

    private short mark;
    private int keySize;
    private int valueSize;
    private byte[] key;
    private byte[] value;

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

    public byte[] encode() {
        int bufLen = keySize + valueSize + ENTRY_HEAD_LEN;
        ByteBuffer buf = ByteBuffer.allocate(bufLen);
        buf.putShort(mark);
        buf.putInt(keySize);
        buf.putInt(valueSize);
        buf.put(key);
        buf.put(value);

        return buf.array();
    }

    public Entry decode(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        short mark = buf.getShort();
        int keySize = buf.getInt();
        int valueSize = buf.getInt();
        byte[] key = new byte[keySize];
        buf.get(key);
        byte[] value = new byte[valueSize];
        buf.get(value);

        return new Entry(mark, keySize, valueSize, key, value);
    }
}
