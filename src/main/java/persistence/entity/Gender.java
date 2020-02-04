package persistence.entity;

import java.util.Random;

public enum Gender {

    MALE,

    FEMALE,

    OTHER;

    public static Gender random() {
        return Gender.values()[new Random().nextInt(Gender.values().length - 1)];
    }
}
