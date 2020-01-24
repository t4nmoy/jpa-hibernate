package persistence.entity;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department extends LongIdEntity {

    @NonNull
    @NotEmpty(message = "title is required")
    @Column(unique = true)
    private String title;

    @NonNull
    @NotEmpty(message = "code is required")
    @Column(unique = true, nullable = false)
    private String code;

    @OneToOne
    private Employee manager;

    @NonNull
    @NotNull(message = "company is required")
    @ManyToOne(optional = false)
    private Company company;

    /**
     * if any entity graph needs to contain more than one <code>EAGER</code> collections
     * then {@link LazyCollection} annotation is needed
     */
    @OneToMany(mappedBy = "department")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Employee> employees;

    @Override
    public String toString() {
        return "Department{" +
                "id='" + this.getId() + '\'' +
                "title='" + title + '\'' +
                '}';
    }
}
