package devops25.releaserangers.coursemgmt_service.service;

import devops25.releaserangers.coursemgmt_service.dto.FavoriteItemDto;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseRepository.findAll()).thenReturn(courses);
        assertEquals(courses, courseService.getAllCourses());
    }

    @Test
    void getCourseById_Found() {
        Course course = new Course();
        when(courseRepository.findById("1")).thenReturn(Optional.of(course));
        assertEquals(course, courseService.getCourseById("1"));
    }

    @Test
    void getCourseById_NotFound() {
        when(courseRepository.findById("1")).thenReturn(Optional.empty());
        assertNull(courseService.getCourseById("1"));
    }

    @Test
    void getCoursesByUserId_ShouldReturnCourses() {
        Course course = new Course();
        course.setUserId("u1");
        when(courseRepository.findAll()).thenReturn(List.of(course));
        List<Course> result = courseService.getCoursesByUserId("u1");
        assertEquals(1, result.size());
        assertEquals("u1", result.get(0).getUserId());
    }

    @Test
    void getFavoriteCoursesByUserId_ShouldReturnFavorites() {
        Course course = new Course();
        course.setId("c1");
        course.setUserId("u1");
        course.setIsFavorite(true);
        course.setName("CourseName");
        course.setEmoji(":)");
        when(courseRepository.findAll()).thenReturn(List.of(course));
        List<FavoriteItemDto> favorites = courseService.getFavoriteCoursesByUserId("u1");
        assertEquals(1, favorites.size());
        assertEquals("c1", favorites.get(0).getId());
    }

    @Test
    void saveCourse_ShouldReturnSavedCourse() {
        Course course = new Course();
        when(courseRepository.save(course)).thenReturn(course);
        assertEquals(course, courseService.saveCourse(course));
    }

    @Test
    void deleteCourse_ExistingCourse() {
        Course course = new Course();
        course.setId("1");
        when(courseRepository.findById("1")).thenReturn(Optional.of(course));
        courseService.deleteCourse(course);
        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void deleteCourse_NonExistingCourse() {
        Course course = new Course();
        course.setId("1");
        when(courseRepository.findById("1")).thenReturn(Optional.empty());
        courseService.deleteCourse(course);
        verify(courseRepository, never()).delete(any());
    }
}
