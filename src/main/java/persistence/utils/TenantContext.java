package persistence.utils;

public class TenantContext {
    private static Long tenantId;

    public static Long getTenantId() {
        return tenantId;
    }

    public static void setTenantId(Long tenant) {
        tenantId = tenant;
    }
}
