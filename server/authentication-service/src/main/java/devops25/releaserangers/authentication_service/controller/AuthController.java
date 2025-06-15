package devops25.releaserangers.authentication_service.controller;

import devops25.releaserangers.authentication_service.model.User;
import devops25.releaserangers.authentication_service.repository.UserRepository;
import devops25.releaserangers.authentication_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        try {
            // Authenticate the user using the provided credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
            String token = jwtUtils.generateToken(user.getEmail());
            User authenticatedUser = userRepository.findByEmail(user.getEmail());
            ResponseCookie responseCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false) // TODO Set to true if using HTTPS
                    .path("/") // Set the path for the cookie
                    .sameSite("Lax") // TODO Set SameSite attribute in production
                    .maxAge(3600) // Set cookie expiration time (1 hour)
                    .build();
            return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body(authenticatedUser);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        // Create new user's account
        User newUser = new User(
                null,
                user.getEmail(),
                user.getName(),
                encoder.encode(user.getPassword()),
                null, // createdAt will be set automatically
                null  // updatedAt will be set automatically
        );
        userRepository.save(newUser);

        try {
            // Authenticate the user after registration
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
            String token = jwtUtils.generateToken(user.getEmail());
            ResponseCookie responseCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false) // TODO Set to true if using HTTPS
                    .path("/") // Set the path for the cookie
                    .sameSite("Lax") // TODO Set SameSite attribute in production
                    .maxAge(3600) // Set cookie expiration time (1 hour)
                    .build();
            return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body(newUser);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserDetails(@CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = jwtUtils.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUserDetails(@RequestBody User user, @CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        String email = jwtUtils.getUsernameFromToken(token);
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (user.getEmail() != null) {
            if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest().body("Error: Email is already in use!");
            }
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(encoder.encode(user.getPassword()));
        }
        userRepository.save(existingUser);
        return ResponseEntity.ok(existingUser);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        String email = jwtUtils.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userRepository.delete(user);
        // Invalidate the cookie by setting it to expire
        ResponseCookie responseCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false) // TODO Set to true if using HTTPS
                .path("/") // Set the path for the cookie
                .sameSite("Lax") // TODO Set SameSite attribute in production
                .maxAge(0) // Set cookie expiration time to 0 to delete it
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body("User deleted successfully");
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signoutUser() {
        // invalidate the cookie by setting it to expire
        ResponseCookie responseCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false) // TODO Set to true if using HTTPS
                .path("/") // Set the path for the cookie
                .sameSite("Lax") // TODO Set SameSite attribute in production
                .maxAge(0) // Set cookie expiration time to 0 to delete it
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body("User logged out successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user.getId().toString());
    }

}