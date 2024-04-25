package org.cramin;

import org.cramin.bitcask.Bitcask;
import org.cramin.model.Person;
import org.cramin.model.Serializable;
import org.cramin.model.University;

import java.io.IOException;

/**
 * @Author: cramin
 * @Date: 2024/4/22 14:20:01
 * @Desc:
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String dataDir = "D:\\WorkSpace\\learn\\mini-bitcask\\src\\main\\resources\\db_file";
        try (Bitcask bitcask = Bitcask.open(dataDir)) {
            Person p1 = new Person("jack", 12);
            Person p2 = new Person("cramin", 20);
            Person p3 = new Person("麦克·阿瑟(McArthur)", 13);

            University u1 = new University("上海大学", "上大路99号", 123.456);
            University u2 = new University("清华大学", "北京市海淀区清华园1号", 112233.334455);
            University u3 = new University("北京大学", "北京市海淀区颐和园路5号", 78963.12464);

//            bitcask.put("p1", p1);
//            bitcask.put("u1", u1);
//            bitcask.put("p2", p2);
//            bitcask.put("u2", u2);
//            bitcask.put("p3", p3);
//            bitcask.put("u3", u3);

            bitcask.merge();

            Serializable p11 = bitcask.get("p1");
            System.out.println(p11);
            Serializable p21 = bitcask.get("p2");
            System.out.println(p21);
            Serializable p31 = bitcask.get("p3");
            System.out.println(p31);
            Serializable u11 = bitcask.get("u1");
            System.out.println(u11);
            Serializable u21 = bitcask.get("u2");
            System.out.println(u21);
            Serializable u31 = bitcask.get("u3");
            System.out.println(u31);

            Serializable p41 = bitcask.get("p4");
            System.out.println(p41);
            Serializable u41 = bitcask.get("u4");
            System.out.println(u41);

//            bitcask.remove("p2");
//            Serializable p22 = bitcask.get("p2");
//            System.out.println(p22);
//            bitcask.remove("u3");
//            Serializable u32 = bitcask.get("u3");
//            System.out.println(u32);
        }

    }
}