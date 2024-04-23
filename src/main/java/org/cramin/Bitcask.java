package org.cramin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public final Map<String, Long> index = new ConcurrentHashMap<>();

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
                System.out.println("数据文件目录不存在，创建目录失败");
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
}
