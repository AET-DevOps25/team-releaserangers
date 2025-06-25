package devops25.releaserangers.coursemgmt_service.controller;

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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CourseService courseService;
    @MockBean
    private ChapterService chapterService;
    @MockBean
    private AuthUtils authUtils;

    @Test
    void getCourses_Unauthenticated_ShouldReturn401() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/courses").cookie(new Cookie("token", "badtoken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCourses_Authenticated_ShouldReturnCourses() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        Course course = new Course();
        course.setId("c1");
        course.setUserId("user1");
        // Simulate saving the course before performing the get request
        when(courseService.getCoursesByUserId(anyString())).thenReturn(List.of(course));
        mockMvc.perform(get("/courses").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isOk());
    }

    @Test
    void getCourseById_NotFound_ShouldReturn404() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        when(courseService.getCourseById("c1")).thenReturn(null);
        mockMvc.perform(get("/courses/c1").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCourseById_Forbidden_ShouldReturn403() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        Course course = new Course();
        course.setUserId("user2");
        when(courseService.getCourseById("c1")).thenReturn(course);
        mockMvc.perform(get("/courses/c1").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isForbidden());
    }
}
