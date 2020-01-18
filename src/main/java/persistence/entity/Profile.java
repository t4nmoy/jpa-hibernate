package persistence.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Gender gender;

    private String notes;

    @OneToOne
    private Employee employee;

    @OneToMany
    private List<Education> educations;

    @Embedded
    private ProfileSettings settings;

    @OneToMany
    private List<Contact> contacts = new ArrayList<>();
}
