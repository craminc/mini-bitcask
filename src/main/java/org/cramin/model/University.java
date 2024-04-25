package org.cramin.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author: cramin
 * @Date: 2024/4/25 9:58:07
 * @Desc:
 */
public class University implements Serializable {

    private String name;

    private String position;

    private double salary;

    public University() {
    }

    public University(String name, String position, double salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }


    @Override
    public byte getSerialVersionUID() {
        return 1;
    }

    @Override
    public byte[] serialize() {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] positionBytes = position.getBytes(StandardCharsets.UTF_8);
        String salaryStr = String.valueOf(salary);
        int point = salaryStr.indexOf(".");
        byte precision = (byte) (point == -1 ? 0 : salaryStr.length() - point + 1);
        long salaryLong = (long) (salary * Math.pow(10, precision));

        ByteBuffer buffer = ByteBuffer.allocate(
                4 + nameBytes.length +
                        4 + positionBytes.length +
                        1 + 8
        );
        buffer.putInt(nameBytes.length);
        buffer.put(nameBytes);
        buffer.putInt(positionBytes.length);
        buffer.put(positionBytes);
        buffer.put(precision);
        buffer.putLong(salaryLong);

        return buffer.array();
    }

    @Override
    public Serializable deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int nameBytesLen = buffer.getInt();
        byte[] nameBytes = new byte[nameBytesLen];
        buffer.get(nameBytes);
        int positionBytesLen = buffer.getInt();
        byte[] positionBytes = new byte[positionBytesLen];
        buffer.get(positionBytes);
        byte precision = buffer.get();
        long salaryLong = buffer.getLong();

        String name = new String(nameBytes, StandardCharsets.UTF_8);
        String position = new String(positionBytes, StandardCharsets.UTF_8);
        double salary = ((double) salaryLong) / Math.pow(10, precision);

        return new University(name, position, salary);
    }

    @Override
    public String toString() {
        return "University{" +
                "name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                '}';
    }

    public static void main(String[] args) {
        University university = new University("上海大学", "上大路99号", 123.456);
        byte[] serialize = university.serialize();
        Serializable deserialize = university.deserialize(serialize);
        System.out.println(deserialize);
    }
}
