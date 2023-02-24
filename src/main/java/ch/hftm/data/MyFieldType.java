package ch.hftm.data;

import java.util.Arrays;

public enum MyFieldType {

    DOUBLE(12),
    ASCII(2),
    LONG(4);

    int type;

    MyFieldType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static MyFieldType getByType(int type) {
        return Arrays.stream(MyFieldType.values()).filter(ms -> ms.getType() == type).findFirst().orElse(null);
    }
}
