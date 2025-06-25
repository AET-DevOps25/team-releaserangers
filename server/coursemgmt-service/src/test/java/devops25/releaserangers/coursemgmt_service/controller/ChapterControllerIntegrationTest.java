package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChapterController.class)
class ChapterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ChapterService chapterService;
    @MockBean
    private CourseService courseService;
    @MockBean
    private AuthUtils authUtils;

    @Test
    void getChapters_Unauthenticated_ShouldReturn401() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/chapters").cookie(new Cookie("token", "badtoken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getChapters_Authenticated_ShouldReturnChapters() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        Chapter chapter = new Chapter();
        chapter.setId("ch1");
        Course course = new Course();
        course.setUserId("user1");
        course.setChapters(Arrays.asList(chapter));
        chapter.setCourse(course);
        when(courseService.getCoursesByUserId(anyString())).thenReturn(List.of(course));
        mockMvc.perform(get("/chapters").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isOk());
    }

    @Test
    void getChapterById_NotFound_ShouldReturn404() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        when(chapterService.getChapterById("ch1")).thenReturn(null);
        mockMvc.perform(get("/chapters/ch1").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChapterById_Forbidden_ShouldReturn403() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        Chapter chapter = new Chapter();
        Course course = new Course();
        course.setUserId("user2");
        chapter.setCourse(course);
        when(chapterService.getChapterById("ch1")).thenReturn(chapter);
        mockMvc.perform(get("/chapters/ch1").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isForbidden());
    }
}
