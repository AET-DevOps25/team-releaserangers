package devops25.releaserangers.coursemgmt_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class AuthUtils {

    @Value("${auth.service.validate.url}")
    private String AUTH_SERVICE_VALIDATE_URL;

    @Autowired
    private RestTemplate restTemplate;

    public Optional<String> validateAndGetUserId(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authHeader);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(AUTH_SERVICE_VALIDATE_URL, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                return Optional.empty();
            }
            return Optional.ofNullable(response.getBody());
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            return Optional.empty();
        }
    }
}
