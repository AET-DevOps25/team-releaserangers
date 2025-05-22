package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.DTO.ChapterPatchRequest;
import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapters")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CourseService courseService;

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

    @PutMapping("/{chapter_id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable String chapter_id,
                                                 @RequestBody Chapter chapterRequest) {
        Chapter existingChapter = chapterService.getChapterById(chapter_id);
        if (existingChapter == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (chapterRequest.getCourse() != null && chapterRequest.getCourse().getId() != null) {
            chapterRequest.setCourse(courseService.getCourseById(chapterRequest.getCourse().getId()));
        }
        
        BeanUtils.copyProperties(chapterRequest, existingChapter, "id", "createdAt", "updatedAt");
        return ResponseEntity.ok(chapterService.saveChapter(existingChapter));
    }

    @PatchMapping("/{chapter_id}")
    public ResponseEntity<Chapter> patchChapter(@PathVariable String chapter_id, @RequestBody ChapterPatchRequest patch) {
        Chapter patched = chapterService.patchChapter(chapter_id, patch);
        return ResponseEntity.ok(patched);
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