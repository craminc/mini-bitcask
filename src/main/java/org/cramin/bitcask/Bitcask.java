package org.cramin.bitcask;

import org.cramin.util.Serializer;
import org.cramin.model.Serializable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: cramin
 * @Date: 2024/4/22 14:21:30
 * @Desc:
 */
public class Bitcask implements Closeable {

    // 数据文件目录
    private final String dirPath;
    // 数据文件
    private DbFile dbFile;
    // 索引
    private final Map<String, Long> index = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

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
        byte[] valueBytes = Serializer.serialize(value);
        Entry entry = new Entry(Entry.PUT, keyBytes, valueBytes);

        lock.lock();
        try {
            long offset = dbFile.offset;
            dbFile.write(entry);
            index.put(key, offset);
        } catch (IOException e) {
            System.err.println("put error");
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Serializable get(String key) {
        // WAL不影响读
        rwLock.readLock().lock();
        try {
            Long offset = index.get(key);
            if (offset == null) {
                return null;
            }
            Entry entry = dbFile.read(offset);
            byte[] valueBytes = entry.value;

            return Serializer.deserialize(valueBytes);
        } catch (IOException e) {
            System.err.println("get error");
            throw new RuntimeException(e);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void remove(String key) {
        lock.lock();
        try {
            if (!index.containsKey(key)) {
                return;
            }
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            Entry entry = new Entry(Entry.DEL, keyBytes, new byte[0]);

            dbFile.write(entry);

            index.remove(key);

        } catch (IOException e) {
            System.err.println("remove error");
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void merge() throws IOException {
        lock.lock();
        try {
            long offset = 0;
            List<Entry> validEntries = new ArrayList<>();
            while (offset < dbFile.offset) {
                Entry entry = this.dbFile.read(offset);

                String key = new String(entry.key, StandardCharsets.UTF_8);
                Long indexOffset = index.get(key);
                if (indexOffset != null && indexOffset == offset) {
                    validEntries.add(entry);
                }

                offset += entry.size();
            }

            DbFile tmpDbFile = new DbFile(dirPath, true);

            for (Entry entry : validEntries) {
                long newOffset = tmpDbFile.offset;
                tmpDbFile.write(entry);
                String key = new String(entry.key, StandardCharsets.UTF_8);
                index.put(key, newOffset);
            }

            // 禁止线程读
            rwLock.writeLock().lock();
            // 关闭文件
            tmpDbFile.close();
            dbFile.close();
            // 文件重命名
            DbFile.removeTmp(dirPath);
            // 重新打开数据文件
            dbFile = new DbFile(dirPath);

        } finally {
            if (rwLock.writeLock().isHeldByCurrentThread()) {
                rwLock.writeLock().unlock();
            }
            lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.dbFile.close();
    }
}
