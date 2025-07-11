package devops25.releaserangers.coursemgmt_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.repository.ChapterRepository;
import devops25.releaserangers.coursemgmt_service.repository.CourseRepository;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ChapterControllerFullIntegrationTest {
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
    private ObjectMapper objectMapper;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private CourseRepository courseRepository;
    @MockitoBean
    private AuthUtils authUtils;

    private static final String ENDPOINT_CHAPTERS = "/chapters";
    private static final String COOKIE_TOKEN = "token";
    private static final String TOKEN_GOOD = "goodtoken";
    private static final String USER_ID_1 = "user1";
    private static final String USER_ID_2 = "user2";

    @BeforeEach
    void setup() {
        chapterRepository.deleteAll();
        courseRepository.deleteAll();
        // Only the good token returns a user, all others return empty
        when(authUtils.validateAndGetUserId(TOKEN_GOOD)).thenReturn(Optional.of(USER_ID_1));
        when(authUtils.validateAndGetUserId(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            if (TOKEN_GOOD.equals(token)) return Optional.of(USER_ID_1);
            return Optional.empty();
        });
    }

    @Test
    @DisplayName("Should create and fetch a chapter for authenticated user (real DB)")
    void createAndFetchChapter_RealDb() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Course for Chapter");
        course = courseRepository.save(course);
        Chapter chapter = new Chapter();
        chapter.setTitle("Integration Chapter");
        chapter.setCourse(course);
        String requestJson = "{" +
                "\"id\":null," +
                "\"title\":\"Integration Chapter\"," +
                "\"content\":null," +
                "\"emoji\":null," +
                "\"isFavorite\":null," +
                "\"createdAt\":null," +
                "\"updatedAt\":null," +
                "\"course\":{" +
                "\"id\":\"" + course.getId() + "\"," +
                "\"userId\":\"" + course.getUserId() + "\"," +
                "\"name\":\"" + course.getName() + "\"," +
                "\"description\":null," +
                "\"createdAt\":null," +
                "\"updatedAt\":null," +
                "\"chapters\":null" +
                "}" +
                "}";
        // Create
        String response = mockMvc.perform(post(ENDPOINT_CHAPTERS)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Chapter created = objectMapper.readValue(response, Chapter.class);
        // Fetch
        String fetchResponse = mockMvc.perform(get(ENDPOINT_CHAPTERS)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Chapter[] fetchedChapters = objectMapper.readValue(fetchResponse, Chapter[].class);
        assertThat(fetchedChapters).hasSize(1);
        Chapter fetched = fetchedChapters[0];
        assertThat(fetched.getId()).isNotNull();
        assertThat(fetched.getTitle()).isEqualTo(created.getTitle());
    }

    @Test
    @DisplayName("Should update a chapter for authenticated user (real DB)")
    void updateChapter_RealDb() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Course for Chapter");
        course = courseRepository.save(course);
        Chapter chapter = new Chapter();
        chapter.setTitle("Original Chapter");
        chapter.setCourse(course);
        chapter = chapterRepository.save(chapter);
        chapter.setTitle("Updated Chapter");
        String updateJson = objectMapper.writeValueAsString(chapter);
        String updateResponse = mockMvc.perform(put(ENDPOINT_CHAPTERS + "/" + chapter.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Chapter updated = objectMapper.readValue(updateResponse, Chapter.class);
        assertThat(updated.getTitle()).isEqualTo("Updated Chapter");
        assertThat(updated.getId()).isEqualTo(chapter.getId());
    }

    @Test
    @DisplayName("Should delete a chapter for authenticated user (real DB)")
    void deleteChapter_RealDb() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Course for Chapter");
        course = courseRepository.save(course);
        Chapter chapter = new Chapter();
        chapter.setTitle("To Delete");
        chapter.setCourse(course);
        chapter = chapterRepository.save(chapter);
        mockMvc.perform(delete(ENDPOINT_CHAPTERS + "/" + chapter.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNoContent());
        assertThat(chapterRepository.findById(chapter.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should return 403 when accessing another user's chapter")
    void forbiddenAccess_RealDb() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Course for Chapter");
        course = courseRepository.save(course);
        Chapter chapter = new Chapter();
        chapter.setTitle("User1's Chapter");
        chapter.setCourse(course);
        chapter = chapterRepository.save(chapter);
        // Try to access as USER_ID_2
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_2));
        mockMvc.perform(get(ENDPOINT_CHAPTERS + "/" + chapter.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isForbidden());
        mockMvc.perform(put(ENDPOINT_CHAPTERS + "/" + chapter.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapter)))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete(ENDPOINT_CHAPTERS + "/" + chapter.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent chapter")
    void updateNonExistentChapter_RealDb() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setId("nonexistent");
        chapter.setTitle("Doesn't exist");
        String updateJson = objectMapper.writeValueAsString(chapter);
        mockMvc.perform(put(ENDPOINT_CHAPTERS + "/nonexistent")
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent chapter")
    void deleteNonExistentChapter_RealDb() throws Exception {
        mockMvc.perform(delete(ENDPOINT_CHAPTERS + "/nonexistent")
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for create")
    void unauthorizedCreateChapter() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Course for Chapter");
        course = courseRepository.save(course);
        String requestJson = "{" +
                "\"id\":null," +
                "\"title\":\"Unauthorized Chapter\"," +
                "\"content\":null," +
                "\"emoji\":null," +
                "\"isFavorite\":null," +
                "\"createdAt\":null," +
                "\"updatedAt\":null," +
                "\"course\":{" +
                "\"id\":\"" + course.getId() + "\"," +
                "\"userId\":\"" + course.getUserId() + "\"," +
                "\"name\":\"" + course.getName() + "\"," +
                "\"description\":null," +
                "\"createdAt\":null," +
                "\"updatedAt\":null," +
                "\"chapters\":null" +
                "}" +
                "}";
        // No token
        mockMvc.perform(post(ENDPOINT_CHAPTERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(post(ENDPOINT_CHAPTERS)
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for update")
    void unauthorizedUpdateChapter() throws Exception {
        String requestJson = "{" +
                "\"id\":\"someid\"," +
                "\"title\":\"Unauthorized Update\"," +
                "\"content\":null," +
                "\"emoji\":null," +
                "\"isFavorite\":null," +
                "\"createdAt\":null," +
                "\"updatedAt\":null," +
                "\"course\":null" +
                "}";
        // No token
        mockMvc.perform(put(ENDPOINT_CHAPTERS + "/someid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(put(ENDPOINT_CHAPTERS + "/someid")
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for delete")
    void unauthorizedDeleteChapter() throws Exception {
        // No token
        mockMvc.perform(delete(ENDPOINT_CHAPTERS + "/someid"))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(delete(ENDPOINT_CHAPTERS + "/someid")
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for fetch")
    void unauthorizedFetchChapters() throws Exception {
        // No token
        mockMvc.perform(get(ENDPOINT_CHAPTERS))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(get(ENDPOINT_CHAPTERS)
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken")))
                .andExpect(status().isUnauthorized());
    }
}
