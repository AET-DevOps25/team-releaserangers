package devops25.releaserangers.coursemgmt_service.service;


import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.repository.ChapterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;

    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

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

    public void deleteChapterById(String id) {
        chapterRepository.deleteById(id);
    }

}
