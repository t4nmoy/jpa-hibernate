package persistence.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class TenantEntityBase extends AuditableEntity {

    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
