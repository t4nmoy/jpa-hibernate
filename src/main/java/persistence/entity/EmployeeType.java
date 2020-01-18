package persistence.entity;

public enum EmployeeType {

    PERMANENT(100), PART_TIME(101), INTERN(102);

    private Integer type;

    EmployeeType(Integer type) {
        this.type = type;
    }

    public Integer getCode() {
        return this.type;
    }
}
