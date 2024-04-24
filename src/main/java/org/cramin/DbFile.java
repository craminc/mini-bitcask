package org.cramin;

import java.io.*;

/**
 * @Author: cramin
 * @Date: 2024/4/22 14:43:52
 * @Desc:
 */
public class DbFile implements Closeable {
    // 文件名
    public static final String FILE_NAME = "bitcask.data";
    // 文件
    public final RandomAccessFile file;
    // 文件偏移量
    public long offset;

    public DbFile(String dirPath) throws IOException {
        String filePath = dirPath + FILE_NAME;
        this.file = new RandomAccessFile(filePath, "rw");
        this.offset = file.length();
    }

    public Entry read(long offset) throws IOException {
        file.seek(offset);
        byte[] header = new byte[Entry.ENTRY_HEAD_LEN];
        file.read(header);

        Entry entry = Entry.decode(header);

        byte[] key = new byte[entry.keySize];
        file.readFully(key);
        byte[] value = new byte[entry.valueSize];
        file.readFully(value);

        entry.key = key;
        entry.value = value;

        return entry;
    }

    public long write(Entry entry) throws IOException {
        byte[] bytes = entry.encode();

        file.seek(offset);
        file.write(bytes);

        this.offset = file.length();

        return this.offset;
    }


    @Override
    public void close() throws IOException {
        if (file != null) {
            file.close();
        }
    }
}
