package devops25.releaserangers.coursemgmt_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.coursemgmt_service.model.Course;
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
class CourseControllerFullIntegrationTest {
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
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CourseRepository courseRepository;
    @MockitoBean
    private AuthUtils authUtils;

    private static final String ENDPOINT_COURSES = "/courses";
    private static final String COOKIE_TOKEN = "token";
    private static final String TOKEN_GOOD = "goodtoken";
    private static final String USER_ID_1 = "user1";

    @BeforeEach
    void setup() {
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
    @DisplayName("Should create and fetch a course for authenticated user (real DB)")
    void createAndFetchCourse_RealDb() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Integration Course");
        String requestJson = objectMapper.writeValueAsString(course);
        // Create
        String response = mockMvc.perform(post(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Course created = objectMapper.readValue(response, Course.class);
        // Fetch
        String fetchResponse = mockMvc.perform(get(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // Compare only relevant fields (ignore timestamps)
        Course[] fetchedCourses = objectMapper.readValue(fetchResponse, Course[].class);
        assertThat(fetchedCourses).hasSize(1);
        Course fetched = fetchedCourses[0];
        // Assert fields except timestamps
        assertThat(fetched.getId()).isNotNull();
        assertThat(fetched.getUserId()).isEqualTo(created.getUserId());
        assertThat(fetched.getName()).isEqualTo(created.getName());
        assertThat(fetched.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should update a course for authenticated user (real DB)")
    void updateCourse_RealDb() throws Exception {
        when(authUtils.validateAndGetUserId(TOKEN_GOOD)).thenReturn(Optional.of(USER_ID_1));
        // Create course
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Original Name");
        String requestJson = objectMapper.writeValueAsString(course);
        String response = mockMvc.perform(post(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Course created = objectMapper.readValue(response, Course.class);
        // Update course
        created.setName("Updated Name");
        String updateJson = objectMapper.writeValueAsString(created);
        String updateResponse = mockMvc.perform(put(ENDPOINT_COURSES + "/" + created.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Course updated = objectMapper.readValue(updateResponse, Course.class);
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getId()).isEqualTo(created.getId());
    }

    @Test
    @DisplayName("Should delete a course for authenticated user (real DB)")
    void deleteCourse_RealDb() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        // Create course
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("To Delete");
        String requestJson = objectMapper.writeValueAsString(course);
        String response = mockMvc.perform(post(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Course created = objectMapper.readValue(response, Course.class);
        // Delete course
        mockMvc.perform(delete(ENDPOINT_COURSES + "/" + created.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNoContent());
        // Verify it's gone
        mockMvc.perform(get(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 403 when accessing another user's course")
    void forbiddenAccess_RealDb() throws Exception {
        // Create course as USER_ID_1
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("User1's Course");
        String requestJson = objectMapper.writeValueAsString(course);
        String response = mockMvc.perform(post(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Course created = objectMapper.readValue(response, Course.class);
        // Try to access as USER_ID_2
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user2"));
        mockMvc.perform(get(ENDPOINT_COURSES + "/" + created.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isForbidden());
        mockMvc.perform(put(ENDPOINT_COURSES + "/" + created.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete(ENDPOINT_COURSES + "/" + created.getId())
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent course")
    void updateNonExistentCourse_RealDb() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        Course course = new Course();
        course.setId("nonexistent");
        course.setUserId(USER_ID_1);
        course.setName("Doesn't exist");
        String updateJson = objectMapper.writeValueAsString(course);
        mockMvc.perform(put(ENDPOINT_COURSES + "/nonexistent")
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent course")
    void deleteNonExistentCourse_RealDb() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        mockMvc.perform(delete(ENDPOINT_COURSES + "/nonexistent")
                .cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for create")
    void unauthorizedCreateCourse() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Unauthorized Course");
        String requestJson = objectMapper.writeValueAsString(course);
        // No token
        mockMvc.perform(post(ENDPOINT_COURSES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(post(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for update")
    void unauthorizedUpdateCourse() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setName("Unauthorized Update");
        String requestJson = objectMapper.writeValueAsString(course);
        // No token
        mockMvc.perform(put(ENDPOINT_COURSES + "/someid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(put(ENDPOINT_COURSES + "/someid")
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for delete")
    void unauthorizedDeleteCourse() throws Exception {
        // No token
        mockMvc.perform(delete(ENDPOINT_COURSES + "/someid"))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(delete(ENDPOINT_COURSES + "/someid")
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when no or invalid token is provided for fetch")
    void unauthorizedFetchCourses() throws Exception {
        // No token
        mockMvc.perform(get(ENDPOINT_COURSES))
                .andExpect(status().isUnauthorized());
        // Invalid token
        mockMvc.perform(get(ENDPOINT_COURSES)
                .cookie(new Cookie(COOKIE_TOKEN, "badtoken")))
                .andExpect(status().isUnauthorized());
    }
}
