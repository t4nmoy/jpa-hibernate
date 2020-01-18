package persistence.entity;

import javax.persistence.*;

@Entity
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
