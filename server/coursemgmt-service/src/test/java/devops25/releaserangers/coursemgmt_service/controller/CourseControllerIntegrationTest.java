package devops25.releaserangers.coursemgmt_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerIntegrationTest {
    // Static variables for commonly used Strings
    private static final String ENDPOINT_COURSES = "/courses";
    private static final String ENDPOINT_COURSE_ID_1 = "/courses/c1";
    private static final String ENDPOINT_COURSE_1_CHAPTERS = "/courses/c1/chapters";
    private static final String ENDPOINT_COURSE_1_PATCH = "/courses/c1";
    private static final String ENDPOINT_COURSE_1_PUT = "/courses/c1";
    private static final String ENDPOINT_COURSE_1_DELETE = "/courses/c1";
    private static final String ENDPOINT_COURSE_1_POST_CHAPTER = "/courses/c1/chapters";
    private static final String ENDPOINT_COURSES_POST = "/courses";
    private static final String COOKIE_TOKEN = "token";
    private static final String TOKEN_BAD = "badtoken";
    private static final String TOKEN_GOOD = "goodtoken";
    private static final String USER_ID_1 = "user1";
    private static final String USER_ID_2 = "user2";
    private static final String COURSE_ID_1 = "c1";
    private static final String COURSE_ID_2 = "c2";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CourseService courseService;
    @MockitoBean
    private ChapterService chapterService;
    @MockitoBean
    private AuthUtils authUtils;
    // @Mock(answer = Answers.RETURNS_DEEP_STUBS) private MeterRegistry meterRegistry;

    @DisplayName("Should return 401 and empty body when unauthenticated")
    void getCourses_Unauthenticated_ShouldReturn401() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get(ENDPOINT_COURSES).cookie(new Cookie(COOKIE_TOKEN, TOKEN_BAD)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Should return courses for authenticated user")
    void getCourses_Authenticated_ShouldReturnCourses() throws Exception {
        Course course = new Course();
        course.setId(COURSE_ID_1);
        course.setUserId(USER_ID_1);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCoursesByUserId(anyString())).thenReturn(List.of(course));

        String expectedJson = objectMapper.writeValueAsString(List.of(course));
        mockMvc.perform(get(ENDPOINT_COURSES).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should return multiple courses for authenticated user")
    void getCourses_Authenticated_ShouldReturnMultipleCourses() throws Exception {
        Course course1 = new Course();
        course1.setId(COURSE_ID_1);
        course1.setUserId(USER_ID_1);

        Course course2 = new Course();
        course2.setId(COURSE_ID_2);
        course2.setUserId(USER_ID_1);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCoursesByUserId(anyString())).thenReturn(List.of(course1, course2));

        String expectedJson = objectMapper.writeValueAsString(List.of(course1, course2));
        mockMvc.perform(get(ENDPOINT_COURSES).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should return 404 and empty body when course not found")
    void getCourseById_NotFound_ShouldReturn404() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID_1)).thenReturn(null);
        mockMvc.perform(get(ENDPOINT_COURSE_ID_1).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Should return 403 and empty body when user is forbidden from accessing course")
    void getCourseById_Forbidden_ShouldReturn403() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_2);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID_1)).thenReturn(course);
        mockMvc.perform(get(ENDPOINT_COURSE_ID_1).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Should return chapters for a course for authenticated user")
    void getChaptersByCourseId_ShouldReturnChapters() throws Exception {
        Course course = new Course();
        course.setId(COURSE_ID_1);
        course.setUserId(USER_ID_1);

        Chapter chapter = new Chapter();
        chapter.setId("ch1");

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID_1)).thenReturn(course);
        when(chapterService.getChaptersByCourseId(COURSE_ID_1)).thenReturn(List.of(chapter));

        String expectedJson = objectMapper.writeValueAsString(List.of(chapter));
        mockMvc.perform(get(ENDPOINT_COURSE_1_CHAPTERS).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should create a course for authenticated user")
    void createCourse_ShouldReturnCreatedCourse() throws Exception {
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setId(COURSE_ID_1);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.saveCourse(any(Course.class))).thenReturn(course);

        String requestJson = objectMapper.writeValueAsString(course);
        String expectedJson = objectMapper.writeValueAsString(course);
        mockMvc.perform(post(ENDPOINT_COURSES_POST).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType("application/json").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should create a chapter in a course for authenticated user")
    void createChapterInCourse_ShouldReturnCreatedChapter() throws Exception {
        Course course = new Course();
        course.setId(COURSE_ID_1);
        course.setUserId(USER_ID_1);
        Chapter chapter = new Chapter();
        chapter.setId("ch1");
        chapter.setCourse(course);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID_1)).thenReturn(course);
        when(chapterService.saveChapter(any(Chapter.class))).thenReturn(chapter);

        String requestJson = objectMapper.writeValueAsString(chapter);
        String expectedJson = objectMapper.writeValueAsString(chapter);
        mockMvc.perform(post(ENDPOINT_COURSE_1_POST_CHAPTER).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType("application/json").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should update a course for authenticated user")
    void updateCourse_ShouldReturnUpdatedCourse() throws Exception {
        Course existingCourse = new Course();
        existingCourse.setId(COURSE_ID_1);
        existingCourse.setUserId(USER_ID_1);

        Course updatedCourse = new Course();
        updatedCourse.setId(COURSE_ID_1);
        updatedCourse.setUserId(USER_ID_1);
        updatedCourse.setName("Updated Name");

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID_1)).thenReturn(existingCourse);
        when(courseService.saveCourse(any(Course.class))).thenReturn(updatedCourse);

        String requestJson = objectMapper.writeValueAsString(updatedCourse);
        String expectedJson = objectMapper.writeValueAsString(updatedCourse);
        mockMvc.perform(put(ENDPOINT_COURSE_1_PUT).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType("application/json").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should patch a course for authenticated user")
    void patchCourse_ShouldReturnPatchedCourse() throws Exception {
        Course existingCourse = new Course();
        existingCourse.setId(COURSE_ID_1);
        existingCourse.setUserId(USER_ID_1);

        Course patchedCourse = new Course();
        patchedCourse.setId(COURSE_ID_1);
        patchedCourse.setUserId(USER_ID_1);
        patchedCourse.setName("Patched Name");

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID_1)).thenReturn(existingCourse);
        when(courseService.saveCourse(any(Course.class))).thenReturn(patchedCourse);

        String requestJson = objectMapper.writeValueAsString(patchedCourse);
        String expectedJson = objectMapper.writeValueAsString(patchedCourse);
        mockMvc.perform(patch(ENDPOINT_COURSE_1_PATCH).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType("application/json").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should delete a course for authenticated user")
    void deleteCourse_ShouldReturnNoContent() throws Exception {
        Course existingCourse = new Course();
        existingCourse.setId(COURSE_ID_1);
        existingCourse.setUserId(USER_ID_1);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID_1)).thenReturn(existingCourse);

        mockMvc.perform(delete(ENDPOINT_COURSE_1_DELETE).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNoContent());
    }

    @TestConfiguration
    static class MeterRegistryTestConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return org.mockito.Mockito.mock(MeterRegistry.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        }
    }
}
