package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.dto.FavoriteItemDto;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    private final CourseService courseService;
    private final ChapterService chapterService;
    private final AuthUtils authUtils;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Exposing service references is acceptable here")
    public FavoritesController(final CourseService courseService, final ChapterService chapterService, final AuthUtils authUtils) {
        this.courseService = courseService;
        this.chapterService = chapterService;
        this.authUtils = authUtils;
    }

    @GetMapping
    public ResponseEntity<List<FavoriteItemDto>> getFavorites(@CookieValue("token") String token) {
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        final String userID = userIDOpt.get();
        final List<FavoriteItemDto> favoriteCourses = courseService.getFavoriteCoursesByUserId(userID);
        final List<FavoriteItemDto> favoriteChapters = chapterService.getFavoriteChaptersByUserId(userID);
        // Combine the favorite items from courses and chapters
        final List<FavoriteItemDto> favoriteItems = Stream.concat(favoriteCourses.stream(), favoriteChapters.stream())
                .sorted((item1, item2) -> {
                    if (item1.getType().equals(item2.getType())) {
                        return item1.getTitle().compareTo(item2.getTitle());
                    }
                    return item1.getType().compareTo(item2.getType());
                })
                .collect(Collectors.toList());
        if (favoriteItems.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(favoriteItems);
    }
}
