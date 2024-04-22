package org.cramin;

import java.io.File;

/**
 * @Author: cramin
 * @Date: 2024/4/22 14:43:52
 * @Desc:
 */
public class DbFile {
    // 文件名
    private static final String fileName = "bitcask.data";

    private final File file;
    private Long offset;

    public DbFile(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + dirPath);
        }
    }
}
