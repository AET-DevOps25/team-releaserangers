package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Course>> getCoursesByUser(@PathVariable String userId) {
        List<Course> courses = courseService.getCoursesByUserId(userId);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable String courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.saveCourse(course));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable String courseId, @RequestBody Course course) {
        Course existingCourse = courseService.getCourseById(courseId);
        if (existingCourse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courseService.saveCourse(course));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCourse(@RequestBody Course course) {
        Course existingCourse = courseService.getCourseById(course.getId());
        if (existingCourse == null) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteCourse(course);
        return ResponseEntity.noContent().build();
    }
}