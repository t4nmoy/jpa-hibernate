package persistence.entity;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum EmployeeType {

    PERMANENT(100), PART_TIME(101), INTERN(102), ON_PROBATION(103);

    private Integer type;

    EmployeeType(Integer type) {
        this.type = type;
    }

    public Integer getCode() {
        return this.type;
    }

    public static EmployeeType anyWithout(EmployeeType type) {
        return Arrays.stream(EmployeeType.values()).filter(empType  -> !empType.equals(type)).collect(Collectors.toList()).get(0);
    }

}
