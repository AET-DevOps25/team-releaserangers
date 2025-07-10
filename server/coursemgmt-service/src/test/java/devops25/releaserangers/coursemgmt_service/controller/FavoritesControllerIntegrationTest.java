package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Transactional
class FavoritesControllerIntegrationTest {
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
    private MockMvc mockMvc;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ChapterService chapterService;
    @MockitoBean
    private AuthUtils authUtils;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void getFavorites_Unauthenticated_ShouldReturn401() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/favorites").cookie(new Cookie("token", "badtoken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getFavorites_Authenticated_ShouldReturnFavorites() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        // Insert favorite course
        jdbcTemplate.update("INSERT INTO courses (course_id, user_id, course_name, course_description, course_emoji, course_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "id1", "user1", "CourseTitle", null, ":)", true);
        // Insert favorite chapter
        jdbcTemplate.update("INSERT INTO chapters (chapter_id, course_id, chapter_title, chapter_content, chapter_emoji, chapter_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "id2", "id1", "ChapterTitle", null, ":D", true);
        String expectedJson = "[" +
            "{\"id\":\"id1\",\"type\":\"course\",\"courseId\":null,\"title\":\"CourseTitle\",\"emoji\":\":)\"}," +
            "{\"id\":\"id2\",\"type\":\"chapter\",\"courseId\":\"id1\",\"title\":\"ChapterTitle\",\"emoji\":\":D\"}" +
        "]";
        mockMvc.perform(get("/favorites").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getFavorites_Authenticated_ShouldReturnOnlyFavorites() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        // Insert favorite course
        jdbcTemplate.update("INSERT INTO courses (course_id, user_id, course_name, course_description, course_emoji, course_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "favCourseId", "user1", "Favorite Course", null, ":)", true);
        // Insert non-favorite course
        jdbcTemplate.update("INSERT INTO courses (course_id, user_id, course_name, course_description, course_emoji, course_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "nonFavCourseId", "user1", "Non-Favorite Course", null, null, false);
        // Insert favorite chapter
        jdbcTemplate.update("INSERT INTO chapters (chapter_id, course_id, chapter_title, chapter_content, chapter_emoji, chapter_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "favChapterId", "favCourseId", "Favorite Chapter", null, ":D", true);
        // Insert non-favorite chapter
        jdbcTemplate.update("INSERT INTO chapters (chapter_id, course_id, chapter_title, chapter_content, chapter_emoji, chapter_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "nonFavChapterId", "nonFavCourseId", "Non-Favorite Chapter", null, null, false);
        String expectedJson = "[" +
            "{\"id\":\"favCourseId\",\"type\":\"course\",\"courseId\":null,\"title\":\"Favorite Course\",\"emoji\":\":)\"}," +
            "{\"id\":\"favChapterId\",\"type\":\"chapter\",\"courseId\":\"favCourseId\",\"title\":\"Favorite Chapter\",\"emoji\":\":D\"}" +
        "]";
        mockMvc.perform(get("/favorites").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getFavorites_Authenticated_DBIntegration_ShouldReturnOnlyFavorites() throws Exception {
        // Ensure users table exists
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (email VARCHAR(255) PRIMARY KEY, name VARCHAR(255), password VARCHAR(255))");
        // Insert user
        jdbcTemplate.update("INSERT INTO users (email, name, password) VALUES (?, ?, ?)", "user@mail.de", "user", "userpass");
        // Insert courses (omit course_id, createdAt, updatedAt)
        jdbcTemplate.update("INSERT INTO courses (course_id, user_id, course_name, course_description, course_emoji, course_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "1", "user1", "Favorite Course", null, null, true);
        jdbcTemplate.update("INSERT INTO courses (course_id, user_id, course_name, course_description, course_emoji, course_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "2", "user1", "Non-Favorite Course", null, null, false);
        // Get generated course IDs
        String favCourseId = jdbcTemplate.queryForObject("SELECT course_id FROM courses WHERE course_name = ?", String.class, "Favorite Course");
        String nonFavCourseId = jdbcTemplate.queryForObject("SELECT course_id FROM courses WHERE course_name = ?", String.class, "Non-Favorite Course");
        // Insert chapters (omit chapter_id, createdAt, updatedAt)
        jdbcTemplate.update("INSERT INTO chapters (chapter_id, course_id, chapter_title, chapter_content, chapter_emoji, chapter_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "3", favCourseId, "Favorite Chapter", null, null, true);
        jdbcTemplate.update("INSERT INTO chapters (chapter_id, course_id, chapter_title, chapter_content, chapter_emoji, chapter_is_favorite) VALUES (?, ?, ?, ?, ?, ?)", "4", nonFavCourseId, "Non-Favorite Chapter", null, null, false);
        // Get generated chapter IDs
        String favChapterId = jdbcTemplate.queryForObject("SELECT chapter_id FROM chapters WHERE chapter_title = ?", String.class, "Favorite Chapter");
        // Mock authentication only
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        String expectedJson = "[" +
            "{\"id\":\"" + favCourseId + "\",\"type\":\"course\",\"courseId\":null,\"title\":\"Favorite Course\",\"emoji\":null}," +
            "{\"id\":\"" + favChapterId + "\",\"type\":\"chapter\",\"courseId\":\"" + favCourseId + "\",\"title\":\"Favorite Chapter\",\"emoji\":null}" +
        "]";
        mockMvc.perform(get("/favorites").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }
}
