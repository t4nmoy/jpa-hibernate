package persistence.entity;

public enum CompanyType {

    CUSTOMER("Customer"), MARKETING("Marketing"), ENGINEERING("Engineering");

    private String code;

    CompanyType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
