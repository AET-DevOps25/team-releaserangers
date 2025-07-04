package devops25.releaserangers.coursemgmt_service.controller;

import devops25.releaserangers.coursemgmt_service.dto.FavoriteItemDto;
import devops25.releaserangers.coursemgmt_service.service.ChapterService;
import devops25.releaserangers.coursemgmt_service.service.CourseService;
import devops25.releaserangers.coursemgmt_service.util.AuthUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoritesController.class)
class FavoritesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CourseService courseService;
    @MockBean
    private ChapterService chapterService;
    @MockBean
    private AuthUtils authUtils;

    @Test
    void getFavorites_Unauthenticated_ShouldReturn401() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/favorites").cookie(new Cookie("token", "badtoken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getFavorites_Authenticated_ShouldReturnFavorites() throws Exception {
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        FavoriteItemDto fav1 = new FavoriteItemDto("id1", "course", null, "CourseTitle", ":)");
        FavoriteItemDto fav2 = new FavoriteItemDto("id2", "chapter", "cid", "ChapterTitle", ":D");
        when(courseService.getFavoriteCoursesByUserId("user1")).thenReturn(List.of(fav1));
        when(chapterService.getFavoriteChaptersByUserId("user1")).thenReturn(List.of(fav2));
        mockMvc.perform(get("/favorites").cookie(new Cookie("token", "goodtoken")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
