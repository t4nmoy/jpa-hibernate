package persistence.entity;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Education extends LongIdEntity {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String instituteName;

    private String degreeName;

}
