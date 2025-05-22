package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private ChapterService chapterService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Course>> getCoursesByUser(@PathVariable String userId) {
        List<Course> courses = courseService.getCoursesByUserId(userId);
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
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

    @GetMapping("/{courseId}/chapters")
    public ResponseEntity<List<Chapter>> getChaptersByCourseId(@PathVariable String courseId) {
        List<Chapter> chapters = chapterService.getChaptersByCourseId(courseId);
        if (chapters.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chapters);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.saveCourse(course));
    }

    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<Chapter> createChapterInCourse(
            @PathVariable String courseId,
            @RequestBody Chapter request) {
        request.setCourse(courseService.getCourseById(courseId));

        Chapter created = chapterService.saveChapter(request);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable String courseId, @RequestBody Course course) {
        Course existingCourse = courseService.getCourseById(courseId);
        if (existingCourse == null) {
            return ResponseEntity.notFound().build();
        }
        BeanUtils.copyProperties(course, existingCourse, "id", "createdAt", "updatedAt");
        return ResponseEntity.ok(courseService.saveCourse(existingCourse));
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<Course> patchCourse(@PathVariable String courseId, @RequestBody Course course) {
        Course existingCourse = courseService.getCourseById(courseId);
        if (existingCourse == null) {
            return ResponseEntity.notFound().build();
        }
        for (Field field : course.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(course);
                if (value != null) {
                    field.set(existingCourse, value);
                }
            } catch (IllegalAccessException e) {
                return ResponseEntity.status(500).build();
            }
        }
        return ResponseEntity.ok(courseService.saveCourse(existingCourse));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String courseId) {
        Course existingCourse = courseService.getCourseById(courseId);
        if (existingCourse == null) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteCourse(existingCourse);
        return ResponseEntity.noContent().build();
    }
}