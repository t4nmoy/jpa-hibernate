package persistence.entity;

public enum CompanyType {

    CUSTOMER("Customer"), PROVIDER("Provider"), RESELLER("Reseller");

    private String code;

    CompanyType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
