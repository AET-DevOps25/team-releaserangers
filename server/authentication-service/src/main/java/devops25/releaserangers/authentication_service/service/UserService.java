package devops25.releaserangers.authentication_service.service;

import devops25.releaserangers.authentication_service.model.User;
import devops25.releaserangers.authentication_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }

    public User updateUser(String email, User update) {
        final User user = userRepository.findByEmail(email);
        if (update.getEmail() != null) {
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
        final User user = userRepository.findByEmail(email);
        userRepository.delete(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
