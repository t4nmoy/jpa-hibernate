package persistence.entity;

import java.io.Serializable;

public abstract class IsEntity<ID> implements Serializable, HasID<ID> {

    private static final long serialVersionUID = 1L;

    public IsEntity() {

    }

    public abstract ID getId();

    public abstract void setId(ID id);

    public boolean isPersisted() {
        return getId() != null;
    }

    public void assertPersisted() {
        if (!this.isPersisted()) {
            throw new IllegalStateException("entity not persisted or loaded properly");
        }
    }

    @Override
    public ID ID() {
        return getId();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof HasID) {
            @SuppressWarnings("rawtypes")
            HasID model = (HasID) other;
            if (this.getId() != null && model.ID() != null && this.getId().equals(model.ID())) {
                return true;
            }
        }
        if (super.equals(other)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = 1;
        if (this instanceof HasID) {
            return prime * result + (getId() == null ? 0 : getId().hashCode());
        }
        return super.hashCode();
    }

}
