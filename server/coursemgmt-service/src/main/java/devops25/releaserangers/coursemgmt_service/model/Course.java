package devops25.releaserangers.coursemgmt_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// JPA annotations to define the entity and its table name
@Table(name = "courses")
@Entity
public class Course {
    @Id
    @Column(name = "course_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "course_name", nullable = false)
    private String name;

    @Column(name = "course_description")
    private String description;
}