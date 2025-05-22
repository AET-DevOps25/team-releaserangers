package devops25.releaserangers.coursemgmt_service.service;


import devops25.releaserangers.coursemgmt_service.DTO.ChapterPatchRequest;
import devops25.releaserangers.coursemgmt_service.model.Chapter;
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

    public Chapter saveChapter(Chapter chapter) {
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
