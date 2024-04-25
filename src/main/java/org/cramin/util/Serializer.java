package org.cramin.util;

import org.cramin.model.Serializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: cramin
 * @Date: 2024/4/24 19:36:55
 * @Desc:
 */
public class Serializer {
    private static final Map<Byte, Serializable> serializableMap = new HashMap<>();

    static {
        // 加载所有实现了 Serializable 方法的类
        List<Class<Serializable>> classes = ClassLoaderUtils.loadImplement(Serializable.class);
        try {
            for (Class<Serializable> clazz : classes) {
                Serializable serializable = clazz.getDeclaredConstructor().newInstance();
                byte serialVersionUID = serializable.getSerialVersionUID();
                serializableMap.put(serialVersionUID, serializable);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] serialize(Serializable obj) {
        byte[] serialize = obj.serialize();
        byte[] bytes = new byte[serialize.length + 1];
        bytes[0] = obj.getSerialVersionUID();
        System.arraycopy(serialize, 0, bytes, 1, serialize.length);

        return bytes;
    }

    public static Serializable deserialize(byte[] bytes) {
        byte serialVersionUID = bytes[0];
        Serializable serializable = serializableMap.get(serialVersionUID);
        byte[] serialize = Arrays.copyOfRange(bytes, 1, bytes.length);

        return serializable.deserialize(serialize);
    }
}
