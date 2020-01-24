package persistence.entity;

public enum Designation {

    HR("Hr"),

    TEAM_LEAD("Team Lead"),

    BASIC_EMPLOYEE("Basic Employee"),

    MANAGER("Manager");

    private String title;

    Designation(String title) {
        this.title = title;
    }
}
