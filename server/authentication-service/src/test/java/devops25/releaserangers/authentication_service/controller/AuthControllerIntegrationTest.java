package devops25.releaserangers.authentication_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.authentication_service.model.User;
import devops25.releaserangers.authentication_service.repository.UserRepository;
import devops25.releaserangers.authentication_service.util.TestUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("release")
            .withPassword("ranger");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("jwt.secret", () -> "08856eb694ef8c41d3ebe9a526597613e149c1762294d749e2f33f98281cf431");
        registry.add("client.url", () -> "http://localhost:3000");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testSignIn() throws Exception {
        User user = TestUtils.createTestUser("test@example.com", "Test User", "password123");
        // Dummy Sign up
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
        // Sign in
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("token"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testSignUp_EmailAlreadyExists() throws Exception {
        User user = TestUtils.createTestUser("exists@example.com", "User", "password123");
        userRepository.save(new User(null, user.getEmail(), user.getName(), passwordEncoder.encode(user.getPassword()), null, null));
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));
    }

    @Test
    void testSignIn_InvalidCredentials() throws Exception {
        User user = TestUtils.createTestUser("invalid@example.com", "User", "wrongpass");
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testGetUserDetails_Unauthorized() throws Exception {
        mockMvc.perform(get("/auth/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserDetails_Authorized() throws Exception {
        User user = TestUtils.createTestUser("getuser@example.com", "Get User", "password123");
        userRepository.save(new User(null, user.getEmail(), user.getName(), passwordEncoder.encode(user.getPassword()), null, null));
        String token = signInAndGetToken(user.getEmail(), "password123");
        mockMvc.perform(get("/auth/user").cookie(new Cookie("token", token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void testUpdateUserDetails() throws Exception {
        User user = TestUtils.createTestUser("update@example.com", "Update User", "password123");
        userRepository.save(new User(null, user.getEmail(), user.getName(), passwordEncoder.encode(user.getPassword()), null, null));
        String token = signInAndGetToken(user.getEmail(), "password123");
        User update = new User();
        update.setName("Updated Name");
        mockMvc.perform(patch("/auth/user")
                .cookie(new Cookie("token", token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = TestUtils.createTestUser("delete@example.com", "Delete User", "password123");
        userRepository.save(new User(null, user.getEmail(), user.getName(), passwordEncoder.encode(user.getPassword()), null, null));
        String token = signInAndGetToken(user.getEmail(), "password123");
        mockMvc.perform(delete("/auth/user").cookie(new Cookie("token", token)))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void testSignOut() throws Exception {
        mockMvc.perform(post("/auth/signout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("token", 0))
                .andExpect(content().string("User logged out successfully"));
    }

    @Test
    void testValidateToken_Authorized() throws Exception {
        User user = TestUtils.createTestUser("validate@example.com", "Validate User", "password123");
        userRepository.save(new User(null, user.getEmail(), user.getName(), passwordEncoder.encode(user.getPassword()), null, null));
        String token = signInAndGetToken(user.getEmail(), "password123");
        mockMvc.perform(get("/auth/validate").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testValidateToken_Unauthorized() throws Exception {
        mockMvc.perform(get("/auth/validate"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSignIn_NonExistentUser() throws Exception {
        User user = TestUtils.createTestUser("nouser@example.com", "No User", "password123");
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() throws Exception {
        User user1 = TestUtils.createTestUser("user1@example.com", "User1", "password123");
        User user2 = TestUtils.createTestUser("user2@example.com", "User2", "password123");
        userRepository.save(new User(null, user1.getEmail(), user1.getName(), passwordEncoder.encode(user1.getPassword()), null, null));
        userRepository.save(new User(null, user2.getEmail(), user2.getName(), passwordEncoder.encode(user2.getPassword()), null, null));
        String token = signInAndGetToken(user1.getEmail(), "password123");
        User update = new User();
        update.setEmail(user2.getEmail());
        mockMvc.perform(patch("/auth/user")
                .cookie(new Cookie("token", token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));
    }

    // Helper method to sign in and get JWT token
    private String signInAndGetToken(String email, String password) throws Exception {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        var result = mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andReturn();
        String setCookie = result.getResponse().getHeader("Set-Cookie");
        if (setCookie == null) return null;
        for (String part : setCookie.split(";")) {
            if (part.trim().startsWith("token=")) {
                return part.trim().substring(6);
            }
        }
        return null;
    }
}
