package devops25.releaserangers.coursemgmt_service.service;


import devops25.releaserangers.coursemgmt_service.DTO.ChapterPatchRequest;
import devops25.releaserangers.coursemgmt_service.DTO.ChapterRequest;
import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.repository.ChapterRepository;
import devops25.releaserangers.coursemgmt_service.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Chapter createChapter(ChapterRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Chapter chapter = new Chapter();
        chapter.setTitle(request.getTitle());
        chapter.setContent(request.getContent());
        chapter.setEmoji(request.getEmoji());
        chapter.setIsFavorite(request.getIsFavorite());
        chapter.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        chapter.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());
        chapter.setCourse(course);

        return chapterRepository.save(chapter);
    }

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }
    
    public Chapter getChapterById(String id) {
        return chapterRepository.findById(id).orElse(null);
    }

    public List<Chapter> getChaptersByCourseId(String courseId) {
        return chapterRepository.findByCourseId(courseId);
    }


    public Chapter updateChapter(String id, ChapterRequest request) {
        Chapter existing = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setEmoji(request.getEmoji());
        existing.setIsFavorite(request.getIsFavorite());
        existing.setUpdatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now());

        return chapterRepository.save(existing);
    }

    public Chapter patchChapter(String id, ChapterPatchRequest patch) {
        Chapter existing = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        if (patch.getIsFavorite() != null)
            existing.setIsFavorite(patch.getIsFavorite());
        if (patch.getEmoji() != null)
            existing.setEmoji(patch.getEmoji());
        if (patch.getTitle() != null)
            existing.setTitle(patch.getTitle());

        existing.setUpdatedAt(LocalDateTime.now());

        return chapterRepository.save(existing);
    }

    public void deleteChapterById(String id) {
        chapterRepository.deleteById(id);
    }

}
