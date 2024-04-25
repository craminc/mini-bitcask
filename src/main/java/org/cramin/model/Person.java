package org.cramin.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author: cramin
 * @Date: 2024/4/25 9:58:07
 * @Desc:
 */
public class Person implements Serializable {

    private String name;

    private int age;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }


    @Override
    public byte getSerialVersionUID() {
        return 0;
    }

    @Override
    public byte[] serialize() {
        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + bytes.length + 4);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.putInt(age);

        return buffer.array();
    }

    @Override
    public Serializable deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int byteLen = buffer.getInt();
        byte[] bytes = new byte[byteLen];
        buffer.get(bytes);

        String name = new String(bytes, StandardCharsets.UTF_8);
        int age = buffer.getInt();

        return new Person(name, age);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public static void main(String[] args) {
        Person johnDoe = new Person("John Doe", 20);
        byte[] serialize = johnDoe.serialize();
        Serializable deserialize = johnDoe.deserialize(serialize);
        System.out.println(deserialize);
    }
}
