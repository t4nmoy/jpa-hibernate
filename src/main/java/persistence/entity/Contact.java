package persistence.entity;


import javax.persistence.Entity;

@Entity
public class Contact extends LongIdEntity {

    private String address;

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
