package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import devops25.releaserangers.coursemgmt_service.util.PatchUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final ChapterService chapterService;
    private final MeterRegistry courseMgmtRegistry;
    private final Counter courseMgmtRequestCounter;
    private final Counter courseMgmtErrorCounter;
    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "Gauge is used to track latency")
    private Gauge courseMgmtLatencyGauge;
    @Getter
    @Setter
    private volatile double currentLatency = 0.0;

    @Autowired
    private AuthUtils authUtils;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Exposing service references is acceptable here")
    public CourseController(final CourseService courseService, final ChapterService chapterService, MeterRegistry courseMgmtRegistry) {
        this.courseService = courseService;
        this.chapterService = chapterService;
        this.courseMgmtRegistry = courseMgmtRegistry;
        this.courseMgmtRegistry.config().commonTags("service", "coursemgmt-service");
        this.courseMgmtRequestCounter = Counter.builder("coursemgmt_service_requests_total")
                .description("Total number of coursemgmt requests")
                .tags("service", "coursemgmt-service")
                .register(courseMgmtRegistry);
        this.courseMgmtErrorCounter = Counter.builder("coursemgmt_service_errors_total")
                .description("Total number of errors in coursemgmt service")
                .tags("service", "coursemgmt-service")
                .register(courseMgmtRegistry);
        this.courseMgmtLatencyGauge = Gauge.builder("coursemgmt_service_current_latency", this, CourseController::getCurrentLatency)
                .description("Current latency of the latest coursemgmt request (ms)")
                .tags("service", "coursemgmt-service")
                .register(courseMgmtRegistry);
    }

    private void updateLatency(long startTime) {
        final long latency = System.nanoTime() - startTime;
        setCurrentLatency(latency / 1_000_000.0);
    }

    private ResponseEntity<Course> validateUserAndGetCourse(String courseId, String token) {
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        final String userID = userIDOpt.get();
        final Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        if (!course.getUserId().equals(userID)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(course);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getCourses(@CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final String userID = userIDOpt.get();
        final List<Course> courses = courseService.getCoursesByUserId(userID);
        if (courses.isEmpty()) {
            updateLatency(startTime);
            return ResponseEntity.noContent().build();
        }
        final List<Course> sortedCourses = courses.stream()
                .sorted(Comparator.comparing(Course::getName, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        updateLatency(startTime);
        return ResponseEntity.ok(sortedCourses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable String courseId, @CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final ResponseEntity<Course> response = validateUserAndGetCourse(courseId, token);
        updateLatency(startTime);
        return response;
    }

    @GetMapping("/{courseId}/chapters")
    public ResponseEntity<List<Chapter>> getChaptersByCourseId(@PathVariable String courseId, @CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(courseResponse.getStatusCode()).body(null);
        }
        final List<Chapter> chapters = chapterService.getChaptersByCourseId(courseId);
        if (chapters.isEmpty()) {
            updateLatency(startTime);
            return ResponseEntity.noContent().build();
        }
        updateLatency(startTime);
        return ResponseEntity.ok(chapters);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course, @CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final String userID = userIDOpt.get();
        course.setUserId(userID);
        updateLatency(startTime);
        return ResponseEntity.ok(courseService.saveCourse(course));
    }

    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<Chapter> createChapterInCourse(
            @PathVariable String courseId,
            @RequestBody Chapter request,
            @CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(courseResponse.getStatusCode()).body(null);
        }
        final Course course = courseResponse.getBody();
        request.setCourse(course);
        updateLatency(startTime);
        return ResponseEntity.ok(chapterService.saveChapter(request));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable String courseId, @RequestBody Course course, @CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return courseResponse;
        }
        final Course existingCourse = Objects.requireNonNull(courseResponse.getBody());
        BeanUtils.copyProperties(course, existingCourse, "id", "createdAt", "updatedAt");
        updateLatency(startTime);
        return ResponseEntity.ok(courseService.saveCourse(existingCourse));
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<Course> patchCourse(@PathVariable String courseId, @RequestBody Course course, @CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        final ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return courseResponse;
        }
        final Course existingCourse = courseResponse.getBody();
        if (existingCourse == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.notFound().build();
        }
        try {
            PatchUtils.applyPatch(course, existingCourse);
            updateLatency(startTime);
            return ResponseEntity.ok(courseService.saveCourse(existingCourse));
        } catch (IllegalAccessException e) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String courseId, @CookieValue(value = "token", required = false) String token) {
        courseMgmtRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(401).build();
        }
        final ResponseEntity<Course> courseResponse = validateUserAndGetCourse(courseId, token);
        if (!courseResponse.getStatusCode().is2xxSuccessful()) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.status(courseResponse.getStatusCode()).build();
        }
        final Course existingCourse = courseResponse.getBody();
        if (existingCourse == null) {
            courseMgmtErrorCounter.increment();
            updateLatency(startTime);
            return ResponseEntity.notFound().build();
        }
        courseService.deleteCourse(existingCourse);
        updateLatency(startTime);
        return ResponseEntity.noContent().build();
    }
}