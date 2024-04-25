package org.cramin.bitcask;

import java.io.*;

/**
 * @Author: cramin
 * @Date: 2024/4/22 14:43:52
 * @Desc:
 */
public class DbFile implements Closeable {
    // 文件名
    public static final String FILE_NAME = "bitcask.data";
    // 临时文件名
    public static final String TMP_FILE_NAME = "bitcask.tmp";
    // 文件
    public final RandomAccessFile file;
    // 文件偏移量
    public long offset;

    public DbFile(String dirPath, boolean tmp) throws IOException {
        String filePath = dirPath + "/" +  (tmp ? TMP_FILE_NAME : FILE_NAME);
        this.file = new RandomAccessFile(filePath, "rw");
        this.offset = file.length();
    }

    public DbFile(String dirPath) throws IOException {
        this(dirPath, false);
    }

    public static void removeTmp(String dirPath) {
        File newFile = new File(dirPath + "/" + TMP_FILE_NAME);
        File oldFile = new File(dirPath + "/" + FILE_NAME);

        // 删除旧文件
        if (oldFile.exists() && !oldFile.delete()) {
            System.err.println("Failed to delete old file: " + oldFile.getAbsolutePath());
        }
        if (newFile.exists() && !newFile.renameTo(oldFile)) {
            System.err.println("Failed to rename new file: " + newFile.getAbsolutePath());
        }
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

    public void write(Entry entry) throws IOException {
        byte[] bytes = entry.encode();

        file.seek(offset);
        file.write(bytes);

        this.offset = file.length();
    }


    @Override
    public void close() throws IOException {
        if (file != null) {
            file.close();
        }
    }
}
