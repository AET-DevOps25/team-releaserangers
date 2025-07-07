package devops25.releaserangers.authentication_service.service;

import devops25.releaserangers.authentication_service.model.User;
import devops25.releaserangers.authentication_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    public User updateUser(String email, User update) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (update.getEmail() != null && !user.getEmail().equals(update.getEmail())) {
            if (userRepository.existsByEmail(update.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(update.getEmail());
        }
        if (update.getName() != null) {
            user.setName(update.getName());
        }
        if (update.getPassword() != null) {
            user.setPassword(update.getPassword());
        }
        return userRepository.save(user);
    }

    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.delete(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
