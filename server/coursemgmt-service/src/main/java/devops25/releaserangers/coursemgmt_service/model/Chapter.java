package devops25.releaserangers.coursemgmt_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@SuppressFBWarnings(justification = "Exposing references is acceptable here")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// JPA annotations to define the entity and its table name
@Table(name = "chapters")
@Entity
public class Chapter {
    @Id
    @Column(name = "chapter_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "chapter_title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Course course;

    @Column(name = "chapter_content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "chapter_emoji")
    private String emoji;

    @Column(name = "chapter_is_favorite")
    private Boolean isFavorite;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
