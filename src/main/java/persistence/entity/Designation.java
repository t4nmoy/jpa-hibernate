package persistence.entity;

public enum Designation {

    HR_MANAGER("Hr Manager"),

    TEAM_LEAD("Team Lead"),

    BASIC_EMPLOYEE("Basic Employee"),

    PROJECT_MANAGER("Project Manager");

    private String title;

    Designation(String title) {
        this.title = title;
    }
}
