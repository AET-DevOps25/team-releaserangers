package devops25.releaserangers.coursemgmt_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChapterController.class)
class ChapterControllerIntegrationTest {
    // Static variables for commonly used Strings
    private static final String ENDPOINT_CHAPTERS = "/chapters";
    private static final String COOKIE_TOKEN = "token";
    private static final String TOKEN_BAD = "badtoken";
    private static final String TOKEN_GOOD = "goodtoken";
    private static final String USER_ID_1 = "user1";
    private static final String USER_ID_2 = "user2";
    protected static final String COURSE_ID = "course1";
    private static final String CHAPTER_ID_1 = "ch1";
    private static final String CHAPTER_ID_2 = "ch2";
    private static final String ENDPOINT_CHAPTER_ID_1 = ENDPOINT_CHAPTERS + "/" + CHAPTER_ID_1;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ChapterService chapterService;
    @MockBean
    private CourseService courseService;
    @MockBean
    private AuthUtils authUtils;

    @Test
    @DisplayName("Should return 401 and empty body when unauthenticated")
    void getChapters_Unauthenticated_ShouldReturn401() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get(ENDPOINT_CHAPTERS).cookie(new Cookie(COOKIE_TOKEN, TOKEN_BAD)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Should return chapters for authenticated user")
    void getChapters_Authenticated_ShouldReturnChapters() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setId(CHAPTER_ID_1);
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setId(COURSE_ID);
        chapter.setCourse(course);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCoursesByUserId(anyString())).thenReturn(List.of(course));
        when(chapterService.getChaptersByCourseId(anyString())).thenReturn(List.of(chapter));
        String expectedJson = objectMapper.writeValueAsString(List.of(chapter));
        mockMvc.perform(get(ENDPOINT_CHAPTERS).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should return multiple chapters for authenticated user")
    void getChapters_Authenticated_ShouldReturnChapters_MultipleChapters() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setId(CHAPTER_ID_1);
        Chapter chapter2 = new Chapter();
        chapter2.setId(CHAPTER_ID_2);
        Course course = new Course();
        course.setUserId(USER_ID_1);
        course.setId(COURSE_ID);
        chapter.setCourse(course);
        chapter2.setCourse(course);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCoursesByUserId(anyString())).thenReturn(List.of(course));
        when(chapterService.getChaptersByCourseId(anyString())).thenReturn(List.of(chapter, chapter2));

        String expectedJson = objectMapper.writeValueAsString(List.of(chapter, chapter2));
        mockMvc.perform(get(ENDPOINT_CHAPTERS).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should return 404 and empty body when chapter not found")
    void getChapterById_NotFound_ShouldReturn404() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(chapterService.getChapterById(CHAPTER_ID_1)).thenReturn(null);
        mockMvc.perform(get(ENDPOINT_CHAPTER_ID_1).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Should return 403 and empty body when user is forbidden from accessing chapter")
    void getChapterById_Forbidden_ShouldReturn403() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        Chapter chapter = new Chapter();
        Course course = new Course();
        course.setUserId(USER_ID_2);
        chapter.setCourse(course);
        when(chapterService.getChapterById(CHAPTER_ID_1)).thenReturn(chapter);
        mockMvc.perform(get(ENDPOINT_CHAPTER_ID_1).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Should create a chapter for authenticated user")
    void createChapter_ShouldReturnCreatedChapter() throws Exception {
        // Initialize objects
        Course course = new Course();
        course.setId(COURSE_ID);
        course.setUserId(USER_ID_1);
        course.setName(COURSE_ID);

        Chapter createdChapter = new Chapter();
        createdChapter.setId(CHAPTER_ID_1);
        createdChapter.setTitle(CHAPTER_ID_1);
        createdChapter.setCourse(course);

        // Set up mocks
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(courseService.getCourseById(COURSE_ID)).thenReturn(course);
        when(chapterService.saveChapter(any(Chapter.class))).thenReturn(createdChapter);

        // Perform request and verify
        String requestJson = "{" +
                "\"id\":\"ch1\"," +
                "\"title\":\"ch1\"," +
                "\"content\":null," +
                "\"emoji\":null," +
                "\"isFavorite\":null," +
                "\"createdAt\":null," +
                "\"updatedAt\":null," +
                "\"course\":{" +
                    "\"id\":\"course1\"," +
                    "\"userId\":\"user1\"," +
                    "\"name\":\"course1\"," +
                    "\"description\":null," +
                    "\"createdAt\":null," +
                    "\"updatedAt\":null," +
                    "\"chapters\":null" +
                "}" +
            "}";
        String expectedJson = objectMapper.writeValueAsString(createdChapter);
        mockMvc.perform(post(ENDPOINT_CHAPTERS).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType("application/json").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));
    }

    @Test
    @DisplayName("Should update a chapter for authenticated user")
    void updateChapter_ShouldReturnUpdatedChapter() throws Exception {
        Chapter chapterRequest = new Chapter();
        chapterRequest.setId(CHAPTER_ID_1);
        chapterRequest.setCourse(new Course());

        Chapter existingChapter = new Chapter();
        existingChapter.setId(CHAPTER_ID_1);

        Course course = new Course();
        course.setId("course1");
        course.setUserId(USER_ID_1);

        existingChapter.setCourse(course);
        Chapter updatedChapter = new Chapter();
        updatedChapter.setId(CHAPTER_ID_1);
        updatedChapter.setCourse(course);

        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(chapterService.getChapterById(CHAPTER_ID_1)).thenReturn(existingChapter);
        when(chapterService.saveChapter(any(Chapter.class))).thenReturn(updatedChapter);
        String requestJson = objectMapper.writeValueAsString(chapterRequest);
        String expectedJson = objectMapper.writeValueAsString(updatedChapter);
        mockMvc.perform(put(ENDPOINT_CHAPTER_ID_1).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType("application/json").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should patch a chapter for authenticated user")
    void patchChapter_ShouldReturnPatchedChapter() throws Exception {
        Chapter patch = new Chapter();
        patch.setId(CHAPTER_ID_1);
        patch.setCourse(new Course());
        Chapter existingChapter = new Chapter();
        existingChapter.setId(CHAPTER_ID_1);
        Course course = new Course();
        course.setId("course1");
        course.setUserId(USER_ID_1);
        existingChapter.setCourse(course);
        Chapter patchedChapter = new Chapter();
        patchedChapter.setId(CHAPTER_ID_1);
        patchedChapter.setCourse(course);
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(chapterService.getChapterById(CHAPTER_ID_1)).thenReturn(existingChapter);
        when(chapterService.saveChapter(any(Chapter.class))).thenReturn(patchedChapter);
        String requestJson = objectMapper.writeValueAsString(patch);
        String expectedJson = objectMapper.writeValueAsString(patchedChapter);
        mockMvc.perform(patch(ENDPOINT_CHAPTER_ID_1).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD))
                .contentType("application/json").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("Should delete a chapter for authenticated user")
    void deleteChapter_ShouldReturnNoContent() throws Exception {
        Chapter existingChapter = new Chapter();
        existingChapter.setId(CHAPTER_ID_1);
        Course course = new Course();
        course.setId("course1");
        course.setUserId(USER_ID_1);
        existingChapter.setCourse(course);
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of(USER_ID_1));
        when(chapterService.getChapterById(CHAPTER_ID_1)).thenReturn(existingChapter);
        mockMvc.perform(delete(ENDPOINT_CHAPTER_ID_1).cookie(new Cookie(COOKIE_TOKEN, TOKEN_GOOD)))
                .andExpect(status().isNoContent());
    }
}
