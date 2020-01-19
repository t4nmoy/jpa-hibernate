package persistence.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> extends IsEntity<ID> {
    private static final long serialVersionUID = 1L;

    @Version
    protected Long version;

    public BaseEntity() {
    }

    public BaseEntity(ID id) {
        setId(id);
    }
}
