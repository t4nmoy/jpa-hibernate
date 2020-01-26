package persistence.entity;


import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PhoneNumber {
    public enum Type {
        HOME,
        OFFICE
    }

    private String number;

    private Type type;

    public PhoneNumber(){

    }

    public PhoneNumber(String number, Type type) {
        this.number = number;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(number, that.number) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, type);
    }
}
