package persistence.entity;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EmployeeType {

    PERMANENT(Short.valueOf("100")),

    PART_TIME(Short.valueOf("101")),

    INTERN(Short.valueOf("102")),

    ON_PROBATION(Short.valueOf("103"));

    private final Short type;

    EmployeeType(Short type) {
        this.type = type;
    }

    public Short getCode() {
        return this.type;
    }

    public static EmployeeType anyWithout(EmployeeType type) {
        return Arrays.stream(EmployeeType.values()).filter(empType  -> !empType.equals(type)).collect(Collectors.toList()).get(0);
    }

}
