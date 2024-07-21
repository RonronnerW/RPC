package com.wang.serializer;

/**
 * @author wanglibin
 * @version 1.0
 */
public enum SerializerEnum {
    JDK("jdk", 0),
    JSON("json", 1),
    KRYO("kryo", 2),
    HESSIAN("hessian", 3);

    private final String serializer; // 第一个字段
    private final int number;      // 第二个字段

    // 构造函数
    SerializerEnum(String serializer, int number) {
        this.serializer = serializer;
        this.number = number;
    }

    public String getSerializer() {
        return serializer;
    }

    public int getNumber() {
        return number;
    }

    // 根据number获取serializer的方法
    public static String getSerializerByValue(int value) {
        for (SerializerEnum serializerEnum : SerializerEnum.values()) {
            if (serializerEnum.getNumber() == value) {
                return serializerEnum.getSerializer();
            }
        }
        return null;
    }

    public static int getNumberBySerializer(String name) {
        for (SerializerEnum serializerEnum : SerializerEnum.values()) {
            if (name.equals(serializerEnum.getSerializer())) {
                return serializerEnum.getNumber();
            }
        }

        return -1;
    }


}
