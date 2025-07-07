package devops25.releaserangers.authentication_service.service;

import devops25.releaserangers.authentication_service.model.User;
import devops25.releaserangers.authentication_service.repository.UserRepository;
import devops25.releaserangers.authentication_service.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        User user = TestUtils.createTestUser("unit@example.com", "Unit Test", "pass");
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.registerUser(user);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailExists() {
        User user = TestUtils.createTestUser("exists@example.com", "User", "pass");
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
    }

    @Test
    void testAuthenticateUser_Success() {
        User user = TestUtils.createTestUser("auth@example.com", "Auth", "pass");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("pass", user.getPassword())).thenReturn(true);
        User result = userService.authenticateUser(user.getEmail(), "pass");
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testAuthenticateUser_Failure() {
        when(userRepository.findByEmail("fail@example.com")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> userService.authenticateUser("fail@example.com", "pass"));
    }

    @Test
    void testUpdateUser_Success() {
        User user = TestUtils.createTestUser("update@example.com", "User", "pass");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        User update = new User();
        update.setName("Updated");
        User result = userService.updateUser(user.getEmail(), update);
        assertEquals("Updated", result.getName());
    }

    @Test
    void testDeleteUser_Success() {
        User user = TestUtils.createTestUser("delete@example.com", "User", "pass");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        doNothing().when(userRepository).delete(user);
        assertDoesNotThrow(() -> userService.deleteUser(user.getEmail()));
        verify(userRepository).delete(user);
    }

    // More unit tests for other scenarios...
}
