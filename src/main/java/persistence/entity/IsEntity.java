package persistence.entity;

import java.io.Serializable;

public abstract class IsEntity<ID> implements Serializable, HasID<ID> {

    private static final long serialVersionUID = 1L;

    IsEntity() {

    }

    protected abstract ID getId();

    protected abstract void setId(ID id);

    boolean isPersisted() {
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
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = 1;
        return prime * result + (getId() == null ? 0 : getId().hashCode());
    }

}
