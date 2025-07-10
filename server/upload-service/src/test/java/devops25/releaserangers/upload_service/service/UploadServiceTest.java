package devops25.releaserangers.upload_service.service;

import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UploadServiceTest {
    @Mock
    private FileRepository fileRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UploadService uploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        uploadService = new UploadService(fileRepository, restTemplate);
    }

    @Test
    void handleUploadedFiles_ThrowsIfNoFiles() {
        assertThatThrownBy(() -> uploadService.handleUploadedFiles(null, "COURSE1", "token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No files provided");
        assertThatThrownBy(() -> uploadService.handleUploadedFiles(new MockMultipartFile[0], "COURSE1", "token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No files provided");
    }

    @Test
    void handleUploadedFiles_ThrowsIfNonPdf() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "dummy".getBytes());
        assertThatThrownBy(() -> uploadService.handleUploadedFiles(new MockMultipartFile[]{file}, "COURSE1", "token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currently only PDF file(s) are allowed");
    }

    @Test
    void handleUploadedFiles_ThrowsIfFileNameNull() {
        MockMultipartFile file = new MockMultipartFile("file", null, "application/pdf", "dummy".getBytes());
        assertThatThrownBy(() -> uploadService.handleUploadedFiles(new MockMultipartFile[]{file}, "COURSE1", "token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File name cannot be empty or null");
    }

    @Test
    void handleUploadedFiles_Successful() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy".getBytes());
        File savedFile = new File();
        savedFile.setFilename("test.pdf");
        when(fileRepository.save(any(File.class))).thenReturn(savedFile);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(new ResponseEntity<>("dummy summary", org.springframework.http.HttpStatus.OK));
        List<File> result = uploadService.handleUploadedFiles(new MockMultipartFile[]{file}, "COURSE1", "token");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFilename()).isEqualTo("test.pdf");
        verify(fileRepository, times(1)).save(any(File.class));
    }
}
