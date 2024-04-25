package org.cramin.model;

/**
 * @Author: cramin
 * @Date: 2024/4/24 11:09:10
 * @Desc:
 */
public interface Serializable {

    byte getSerialVersionUID();

    byte[] serialize();

    Serializable deserialize(byte[] data);
}
