package devops25.releaserangers.coursemgmt_service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AuthUtilsTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AuthUtils authUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the private field AUTH_SERVICE_VALIDATE_URL
        try {
            java.lang.reflect.Field field = AuthUtils.class.getDeclaredField("AUTH_SERVICE_VALIDATE_URL");
            field.setAccessible(true);
            field.set(authUtils, "http://mock-url");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validateAndGetUserId_ValidToken() {
        ResponseEntity<String> response = new ResponseEntity<>("userId", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class))).thenReturn(response);
        Optional<String> result = authUtils.validateAndGetUserId("token");
        assertTrue(result.isPresent());
        assertEquals("userId", result.get());
    }

    @Test
    void validateAndGetUserId_InvalidToken() {
        ResponseEntity<String> response = new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class))).thenReturn(response);
        Optional<String> result = authUtils.validateAndGetUserId("token");
        assertTrue(result.isEmpty());
    }

    @Test
    void validateAndGetUserId_Exception() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class))).thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        Optional<String> result = authUtils.validateAndGetUserId("token");
        assertTrue(result.isEmpty());
    }
}
