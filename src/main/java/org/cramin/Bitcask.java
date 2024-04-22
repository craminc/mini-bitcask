package org.cramin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: cramin
 * @Date: 2024/4/22 14:21:30
 * @Desc:
 */
public class Bitcask {

    private final Map<String, Long> index = new ConcurrentHashMap<>();
    // 数据文件目录
    private final String dirPath;
    private final DbFile dbFile;

    public Bitcask(String dirPath) {
        this.dirPath = dirPath;
        this.dbFile = new DbFile(dirPath);
    }
}
