package devops25.releaserangers.coursemgmt_service.service;

import devops25.releaserangers.coursemgmt_service.dto.FavoriteItemDto;
import devops25.releaserangers.coursemgmt_service.model.Chapter;
import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.repository.ChapterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class ChapterServiceTest {
    @Mock
    private ChapterRepository chapterRepository;
    @InjectMocks
    private ChapterService chapterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveChapter_ShouldReturnSavedChapter() {
        Chapter chapter = new Chapter();
        when(chapterRepository.save(chapter)).thenReturn(chapter);
        assertEquals(chapter, chapterService.saveChapter(chapter));
    }

    @Test
    void getAllChapters_ShouldReturnAllChapters() {
        List<Chapter> chapters = List.of(new Chapter(), new Chapter());
        when(chapterRepository.findAll()).thenReturn(chapters);
        assertEquals(chapters, chapterService.getAllChapters());
    }

    @Test
    void getChapterById_Found() {
        Chapter chapter = new Chapter();
        when(chapterRepository.findById("1")).thenReturn(Optional.of(chapter));
        assertEquals(chapter, chapterService.getChapterById("1"));
    }

    @Test
    void getChapterById_NotFound() {
        when(chapterRepository.findById("1")).thenReturn(Optional.empty());
        assertNull(chapterService.getChapterById("1"));
    }

    @Test
    void getChaptersByCourseId_ShouldReturnChapters() {
        List<Chapter> chapters = List.of(new Chapter());
        when(chapterRepository.findByCourseId("c1")).thenReturn(chapters);
        assertEquals(chapters, chapterService.getChaptersByCourseId("c1"));
    }

    @Test
    void getFavoriteChaptersByUserId_ShouldReturnFavorites() {
        Course course = new Course();
        course.setId("c1");
        course.setUserId("u1");
        Chapter chapter = new Chapter();
        chapter.setId("ch1");
        chapter.setCourse(course);
        chapter.setIsFavorite(true);
        chapter.setTitle("title");
        chapter.setEmoji(":)");
        when(chapterRepository.findAll()).thenReturn(List.of(chapter));
        List<FavoriteItemDto> favorites = chapterService.getFavoriteChaptersByUserId("u1");
        assertEquals(1, favorites.size());
        assertEquals("ch1", favorites.get(0).getId());
    }

    @Test
    void deleteChapterById_ShouldCallRepository() {
        chapterService.deleteChapterById("id");
        verify(chapterRepository, times(1)).deleteById("id");
    }
}

