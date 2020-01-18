package persistence.entity;

import javax.persistence.*;

@Entity
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;

    private ContactType contactType;

    @ManyToOne
    private Profile profile;

}
