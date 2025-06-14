package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import devops25.releaserangers.coursemgmt_service.util.PatchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final ChapterService chapterService;


    @Autowired
    private AuthUtils authUtils;

    public CourseController(CourseService courseService, ChapterService chapterService) {
        this.courseService = courseService;
        this.chapterService = chapterService;
    }

    private ResponseEntity<Course> validateUserAndGetCourse(String courseId, String token) {
        Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        String userID = userIDOpt.get();
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        if (!course.getUserId().equals(userID)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(course);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getCourses(@CookieValue("token") String token) {
        Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        String userID = userIDOpt.get();
        List<Course> courses = courseService.getCoursesByUserId(userID);
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable String courseId, @CookieValue("token") String token) {
        return validateUserAndGetCourse(courseId, token);
    }

    @GetMapping("/{courseId}/chapters")
    public ResponseEntity<List<Chapter>> getChaptersByCourseId(@PathVariable String courseId, @CookieValue("token") String token) {
        ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(courseResponse.getStatusCode()).body(null);
        }
        List<Chapter> chapters = chapterService.getChaptersByCourseId(courseId);
        if (chapters.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chapters);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course, @CookieValue("token") String token) {
        Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        String userID = userIDOpt.get();
        course.setUserId(userID);
        return ResponseEntity.ok(courseService.saveCourse(course));
    }

    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<Chapter> createChapterInCourse(
            @PathVariable String courseId,
            @RequestBody Chapter request,
            @CookieValue("token") String token) {
        ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(courseResponse.getStatusCode()).body(null);
        }
        Course course = courseResponse.getBody();
        request.setCourse(course);

        Chapter created = chapterService.saveChapter(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable String courseId, @RequestBody Course course, @CookieValue("token") String token) {
        ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            return courseResponse;
        }
        Course existingCourse = Objects.requireNonNull(courseResponse.getBody());
        BeanUtils.copyProperties(course, existingCourse, "id", "createdAt", "updatedAt");
        return ResponseEntity.ok(courseService.saveCourse(existingCourse));
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<Course> patchCourse(@PathVariable String courseId, @RequestBody Course course, @CookieValue("token") String token) {
        ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            return courseResponse;
        }
        Course existingCourse = courseResponse.getBody();
        try {
            PatchUtils.applyPatch(course, existingCourse);
            return ResponseEntity.ok(courseService.saveCourse(existingCourse));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String courseId, @CookieValue("token") String token) {
        ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(courseResponse.getStatusCode()).build();
        }
        Course existingCourse = courseResponse.getBody();
        if (existingCourse == null) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteCourse(existingCourse);
        return ResponseEntity.noContent().build();
    }
}