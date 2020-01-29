package persistence.entity;

public enum ContactType {

    HOME("home"), OFFICE("office"), MOBILE("mobile"), OTHER("other");

    private final String title;

    ContactType(String title) {
        this.title = title;
    }
}
