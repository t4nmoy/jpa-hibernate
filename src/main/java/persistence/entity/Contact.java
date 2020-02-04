package persistence.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Contact extends LongIdEntity {

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type")
    private ContactType contactType;

    public Contact() {

    }

    private Contact(ContactType contactType, String address) {
        this.contactType = contactType;
        this.address = address;
    }

    public static Contact of(ContactType contactType, String address) {
        return new Contact(contactType, address);
    }
}
