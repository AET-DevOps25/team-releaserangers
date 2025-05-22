package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.PatchUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapters")
public class ChapterController {

    private final ChapterService chapterService;
    private final CourseService courseService;

    public ChapterController(ChapterService chapterService, CourseService courseService) {
        this.chapterService = chapterService;
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Chapter>> getAllChapters() {
        List<Chapter> chapters = chapterService.getAllChapters();
        if (chapters.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/{chapter_id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable String chapter_id) {
        Chapter chapter = chapterService.getChapterById(chapter_id);
        if (chapter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(chapter);
    }

    @PostMapping
    public ResponseEntity<Chapter> createChapter(@RequestBody Chapter chapterRequest) {
        Chapter created = chapterService.saveChapter(chapterRequest);
        return ResponseEntity.ok(created);
    }

    private void checkForCourseIdAndSetCourse(Chapter chapter) {
        if (chapter.getCourse() != null && chapter.getCourse().getId() != null) {
            chapter.setCourse(courseService.getCourseById(chapter.getCourse().getId()));
        }
    }

    @PutMapping("/{chapter_id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable String chapter_id,
                                                 @RequestBody Chapter chapterRequest) {
        Chapter existingChapter = chapterService.getChapterById(chapter_id);
        if (existingChapter == null) {
            return ResponseEntity.notFound().build();
        }
        checkForCourseIdAndSetCourse(chapterRequest);
        BeanUtils.copyProperties(chapterRequest, existingChapter, "id", "createdAt", "updatedAt");
        return ResponseEntity.ok(chapterService.saveChapter(existingChapter));
    }

    @PatchMapping("/{chapter_id}")
    public ResponseEntity<Chapter> patchChapter(@PathVariable String chapter_id, @RequestBody Chapter patch) {
        Chapter patched = chapterService.getChapterById(chapter_id);
        if (patched == null) {
            return ResponseEntity.notFound().build();
        }
        checkForCourseIdAndSetCourse(patch);
        try {
            PatchUtils.applyPatch(patch, patched);
            return ResponseEntity.ok(chapterService.saveChapter(patched));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{chapter_id}")
    public ResponseEntity<Void> deleteChapterById(@PathVariable String chapter_id) {
        Chapter existing_chapter = chapterService.getChapterById(chapter_id);
        if (existing_chapter == null) {
            return ResponseEntity.notFound().build();
        }
        chapterService.deleteChapterById(chapter_id);
        return ResponseEntity.noContent().build();
    }
}