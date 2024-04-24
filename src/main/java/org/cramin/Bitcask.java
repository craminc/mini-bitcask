package org.cramin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: cramin
 * @Date: 2024/4/22 14:21:30
 * @Desc:
 */
public class Bitcask {

    // 数据文件目录
    public final String dirPath;
    // 数据文件
    public final DbFile dbFile;
    // 索引
    public final Map<String, Long> index = new HashMap<>();

    private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

    private Bitcask(String dirPath) throws IOException {
        this.dirPath = dirPath;
        this.dbFile = new DbFile(dirPath);
    }

    public static Bitcask open(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            // 如果数据文件目录不存在则创建
            if (dir.mkdirs()) {
                System.out.println("数据文件目录不存在，创建目录：" + dir.getAbsolutePath());
            } else {
                System.err.println("数据文件目录不存在，创建目录失败");
                throw new IOException();
            }
        }

        Bitcask bitcask = new Bitcask(dir.getAbsolutePath());
        bitcask.loadIndex();

        return bitcask;
    }

    private void loadIndex() throws IOException {
        long offset = 0;
        while (offset < dbFile.offset) {
            Entry entry = this.dbFile.read(offset);

            String key = new String(entry.key, StandardCharsets.UTF_8);

            if (entry.mark == Entry.DEL) {
                this.index.remove(key);
            } else {
                this.index.put(key, offset);
            }

            offset += entry.size();
        }
    }

    public void put(String key, Serializable value) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.serialize();
        Entry entry = new Entry(Entry.PUT, keyBytes, valueBytes);

        rwlock.writeLock().lock();
        try {
            long offset = dbFile.write(entry);
            index.put(key, offset);
        } catch (IOException e) {
            System.err.println("put error");
            throw new RuntimeException(e);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public Serializable get(String key) {
        rwlock.readLock().lock();
        try {
            Long offset = index.get(key);
            if (offset == null) {
                return null;
            }
            Entry entry = dbFile.read(offset);
            byte[] valueBytes = entry.value;


        } catch (IOException e) {
            System.err.println("get error");
            throw new RuntimeException(e);
        } finally {
            rwlock.readLock().unlock();
        }
    }
}
