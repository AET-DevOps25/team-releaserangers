package devops25.releaserangers.coursemgmt_service.service;

import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CourseServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("release")
            .withPassword("ranger");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("client.url", () -> "http://localhost:3000");
    }

    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseRepository courseRepository;

    private static final String USER_ID_1 = "user1";

    @BeforeEach
    void setup() {
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve a course via service layer (real DB)")
    void saveAndRetrieveCourse() {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Service Layer Course");
        Course saved = courseService.saveCourse(course);
        List<Course> found = courseService.getCoursesByUserId(USER_ID_1);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Service Layer Course");
        assertThat(found.get(0).getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Should update a course via service layer (real DB)")
    void updateCourse() {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Original Name");
        Course saved = courseService.saveCourse(course);
        saved.setName("Updated Name");
        Course updated = courseService.saveCourse(saved);
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Should delete a course via service layer (real DB)")
    void deleteCourse() {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("To Delete");
        Course saved = courseService.saveCourse(course);
        courseService.deleteCourse(saved);
        List<Course> found = courseService.getCoursesByUserId(USER_ID_1);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when retrieving courses for non-existent user")
    void getCoursesByNonExistentUser() {
        List<Course> found = courseService.getCoursesByUserId("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return false when deleting non-existent course")
    void deleteNonExistentCourse() {
        boolean thrown = false;
        try {
            courseService.deleteCourse(new Course());
        } catch (Exception e) {
            thrown = true;
        }
        assertThat(thrown).isTrue();
    }

}
