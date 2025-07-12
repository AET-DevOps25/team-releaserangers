package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import devops25.releaserangers.coursemgmt_service.util.PatchUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/chapters")
public class ChapterController {

    private final ChapterService chapterService;
    private final CourseService courseService;

    @Autowired
    private AuthUtils authUtils;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Exposing service references is acceptable here")
    public ChapterController(final ChapterService chapterService, final CourseService courseService) {
        this.chapterService = chapterService;
        this.courseService = courseService;
    }

    private ResponseEntity<Chapter> validateUserAndGetChapter(String chapterId, String token) {
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        final String userID = userIDOpt.get();
        final Chapter chapter = chapterService.getChapterById(chapterId);
        if (chapter == null) {
            return ResponseEntity.notFound().build();
        }
        if (!chapter.getCourse().getUserId().equals(userID)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(chapter);
    }

    @GetMapping
    public ResponseEntity<List<Chapter>> getChapters(@CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).body(null);
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        final String userID = userIDOpt.get();
        final List<Chapter> chapters = courseService.getCoursesByUserId(userID).stream()
                .flatMap(course -> chapterService.getChaptersByCourseId(course.getId()).stream())
                .toList();
        if (chapters.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/{chapter_id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable String chapter_id, @CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).body(null);
        }
        return validateUserAndGetChapter(chapter_id, token);
    }

    @PostMapping
    public ResponseEntity<Chapter> createChapter(@RequestBody Chapter chapterRequest,
                                                 @CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).body(null);
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        final String userID = userIDOpt.get();
        checkForCourseIdAndSetCourse(chapterRequest);
        if (chapterRequest.getCourse() == null || chapterRequest.getCourse().getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        final Course course = courseService.getCourseById(chapterRequest.getCourse().getId());
        if (course == null || !course.getUserId().equals(userID)) {
            return ResponseEntity.status(403).body(null);
        }
        chapterRequest.setCourse(course);
        final Chapter created = chapterService.saveChapter(chapterRequest);
        return ResponseEntity.ok(created);
    }

    private void checkForCourseIdAndSetCourse(Chapter chapter) {
        if (chapter.getCourse() != null && chapter.getCourse().getId() != null) {
            chapter.setCourse(courseService.getCourseById(chapter.getCourse().getId()));
        }
    }

    @PutMapping("/{chapter_id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable String chapter_id,
                                                 @RequestBody Chapter chapterRequest,
                                                 @CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).body(null);
        }
        final ResponseEntity<Chapter> validationResponse = validateUserAndGetChapter(chapter_id, token);
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }
        final Chapter existingChapter = Objects.requireNonNull(validationResponse.getBody());
        checkForCourseIdAndSetCourse(chapterRequest);
        BeanUtils.copyProperties(chapterRequest, existingChapter, "id", "createdAt", "updatedAt");
        return ResponseEntity.ok(chapterService.saveChapter(existingChapter));
    }

    @PatchMapping("/{chapter_id}")
    public ResponseEntity<Chapter> patchChapter(@PathVariable String chapter_id, @RequestBody Chapter patch,
                                                @CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).body(null);
        }
        final ResponseEntity<Chapter> validationResponse = validateUserAndGetChapter(chapter_id, token);
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }
        final Chapter patched = Objects.requireNonNull(validationResponse.getBody());
        checkForCourseIdAndSetCourse(patch);
        try {
            PatchUtils.applyPatch(patch, patched);
            return ResponseEntity.ok(chapterService.saveChapter(patched));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{chapter_id}")
    public ResponseEntity<Void> deleteChapterById(@PathVariable String chapter_id,
                                                  @CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        final ResponseEntity<Chapter> validationResponse = validateUserAndGetChapter(chapter_id, token);
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validationResponse.getStatusCode()).build();
        }
        final Chapter existing_chapter = chapterService.getChapterById(chapter_id);
        if (existing_chapter == null) {
            return ResponseEntity.notFound().build();
        }
        chapterService.deleteChapterById(chapter_id);
        return ResponseEntity.noContent().build();
    }
}