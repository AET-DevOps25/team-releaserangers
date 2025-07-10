package devops25.releaserangers.authentication_service.util;

import devops25.releaserangers.authentication_service.model.User;

public class TestUtils {
    public static User createTestUser(String email, String name, String password) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        return user;
    }
}

