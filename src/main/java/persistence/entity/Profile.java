package persistence.entity;

import javax.persistence.*;
import java.util.List;

@Entity
class Profile extends LongIdEntity {

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    private String notes;

    @OneToOne
    private Employee employee;

    @OneToMany
    private List<Education> educations;

    @Embedded
    private ProfileSettings settings;
}
