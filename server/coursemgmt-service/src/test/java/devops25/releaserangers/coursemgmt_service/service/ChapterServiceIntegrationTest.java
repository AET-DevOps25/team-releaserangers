package devops25.releaserangers.coursemgmt_service.service;

import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.repository.ChapterRepository;
import devops25.releaserangers.coursemgmt_service.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class ChapterServiceIntegrationTest {
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
    }

    @Autowired
    private ChapterService chapterService;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private CourseRepository courseRepository;

    private static final String USER_ID_1 = "user1";

    @BeforeEach
    void setup() {
        chapterRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve a chapter via service layer (real DB)")
    void saveAndRetrieveChapter() {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Service Layer Course");
        course = courseRepository.save(course);
        Chapter chapter = new Chapter();
        chapter.setTitle("Service Layer Chapter");
        chapter.setCourse(course);
        Chapter saved = chapterService.saveChapter(chapter);
        List<Chapter> found = chapterService.getChaptersByCourseId(course.getId());
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Service Layer Chapter");
        assertThat(found.get(0).getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Should update a chapter via service layer (real DB)")
    void updateChapter() {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Service Layer Course");
        course = courseRepository.save(course);
        Chapter chapter = new Chapter();
        chapter.setTitle("Original Chapter");
        chapter.setCourse(course);
        chapter = chapterService.saveChapter(chapter);
        chapter.setTitle("Updated Chapter");
        Chapter updated = chapterService.saveChapter(chapter);
        assertThat(updated.getTitle()).isEqualTo("Updated Chapter");
        assertThat(updated.getId()).isEqualTo(chapter.getId());
    }

    @Test
    @DisplayName("Should delete a chapter via service layer (real DB)")
    void deleteChapter() {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Service Layer Course");
        course = courseRepository.save(course);
        Chapter chapter = new Chapter();
        chapter.setTitle("To Delete");
        chapter.setCourse(course);
        chapter = chapterService.saveChapter(chapter);
        chapterService.deleteChapterById(chapter.getId());
        List<Chapter> found = chapterService.getChaptersByCourseId(course.getId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when retrieving chapters for non-existent course")
    void getChaptersByNonExistentCourse() {
        List<Chapter> found = chapterService.getChaptersByCourseId("nonexistent");
        assertThat(found).isEmpty();
    }
}
