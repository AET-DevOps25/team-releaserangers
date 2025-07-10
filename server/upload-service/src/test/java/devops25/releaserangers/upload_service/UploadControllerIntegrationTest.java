package devops25.releaserangers.upload_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.repository.FileRepository;
import devops25.releaserangers.upload_service.util.AuthUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class UploadControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3-alpine")
            .withDatabaseName("devops25_db")
            .withUsername("release")
            .withPassword("ranger");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthUtils authUtils;

    @MockitoBean
    private RestTemplate restTemplate;

    private static final String VALID_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        fileRepository.deleteAll();
        // By default, mock AuthUtils to always return a user id for any token
        when(authUtils.validateAndGetUserId(anyString())).thenReturn(Optional.of("user1"));
        // Mock summary service endpoint
        when(restTemplate.postForEntity(contains("8084/summarize"), any(), eq(String.class)))
            .thenReturn(new org.springframework.http.ResponseEntity<>("dummy summary", org.springframework.http.HttpStatus.OK));
    }

    @Test
    void uploadFiles_UnauthorizedWithoutToken() throws Exception {
        // For this test, AuthUtils should not be called, so no need to mock
        MockMultipartFile file = new MockMultipartFile("files", "test.pdf", "application/pdf", "dummy content".getBytes());
        mockMvc.perform(multipart("/upload/COURSE1").file(file))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadFiles_UnauthorizedWithInvalidToken() throws Exception {
        when(authUtils.validateAndGetUserId("invalid")).thenReturn(Optional.empty());
        MockMultipartFile file = new MockMultipartFile("files", "test.pdf", "application/pdf", "dummy content".getBytes());
        mockMvc.perform(multipart("/upload/COURSE1").file(file).cookie(new Cookie("token", "invalid")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadFiles_BadRequestForNonPdf() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain", "dummy content".getBytes());
        mockMvc.perform(multipart("/upload/COURSE1").file(file).cookie(new Cookie("token", VALID_TOKEN)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadFiles_Successful() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.pdf", "application/pdf", "dummy content".getBytes());
        mockMvc.perform(multipart("/upload/COURSE1").file(file).cookie(new Cookie("token", VALID_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename", is("test.pdf")))
                .andExpect(jsonPath("$[0].courseId", is("COURSE1")));
    }

    @Test
    void getAllFiles_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/upload"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllFiles_UnauthorizedWithInvalidToken() throws Exception {
        when(authUtils.validateAndGetUserId("invalid")).thenReturn(Optional.empty());
        mockMvc.perform(get("/upload").cookie(new Cookie("token", "invalid")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllFiles_Empty() throws Exception {
        mockMvc.perform(get("/upload").cookie(new Cookie("token", VALID_TOKEN)))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllFiles_Successful() throws Exception {
        File file = new File();
        file.setFilename("test.pdf");
        file.setContentType("application/pdf");
        file.setCourseId("COURSE1");
        file.setData("dummy content".getBytes());
        fileRepository.save(file);
        mockMvc.perform(get("/upload").cookie(new Cookie("token", VALID_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename", is("test.pdf")));
    }

    @Test
    void getFilesByCourseId_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/upload/COURSE1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getFilesByCourseId_UnauthorizedWithInvalidToken() throws Exception {
        when(authUtils.validateAndGetUserId("invalid")).thenReturn(Optional.empty());
        mockMvc.perform(get("/upload/COURSE1").cookie(new Cookie("token", "invalid")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getFilesByCourseId_Empty() throws Exception {
        mockMvc.perform(get("/upload/COURSE1").cookie(new Cookie("token", VALID_TOKEN)))
                .andExpect(status().isNoContent());
    }

    @Test
    void getFilesByCourseId_Successful() throws Exception {
        File file = new File();
        file.setFilename("test.pdf");
        file.setContentType("application/pdf");
        file.setCourseId("COURSE1");
        file.setData("dummy content".getBytes());
        fileRepository.save(file);
        mockMvc.perform(get("/upload/COURSE1").cookie(new Cookie("token", VALID_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename", is("test.pdf")))
                .andExpect(jsonPath("$[0].courseId", is("COURSE1")));
    }
}
