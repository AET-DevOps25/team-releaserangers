package devops25.releaserangers.authentication_service.controller;

import devops25.releaserangers.authentication_service.model.User;
import devops25.releaserangers.authentication_service.security.JwtUtil;
import devops25.releaserangers.authentication_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    UserService userService;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;
    @Value("${client.url}")
    private String clientUrl;

    private Boolean isHttps() {
        // Check if the application is running in a secure context (HTTPS)
        return "https".equalsIgnoreCase(clientUrl.split("://")[0]);
    }

    private String getCookieDomain() {
        if (!isHttps() && clientUrl.contains("localhost")) {
            return "localhost";
        }
        final String[] parts = clientUrl.split("://");
        if (parts.length > 1) {
            final String host = parts[1].split("/")[0];
            final String[] hostParts = host.split("\\.");
            return hostParts[hostParts.length - 2] + "." + hostParts[hostParts.length - 1];
        }
        return null;
    }

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

            final String token = jwtUtils.generateToken(user.getEmail());
            final User authenticatedUser = userService.findByEmail(user.getEmail());
            final String domain = getCookieDomain();
            final ResponseCookie responseCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(isHttps()) 
                    .path("/")
                    .domain(domain)
                    .sameSite(isHttps() ? "None" : "Lax")
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
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        // Create new user's account
        final User newUser = new User(
                null,
                user.getEmail(),
                user.getName(),
                encoder.encode(user.getPassword()),
                null, // createdAt will be set automatically
                null  // updatedAt will be set automatically
        );
        userService.registerUser(newUser);

        try {
            // Authenticate the user after registration
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
            final String token = jwtUtils.generateToken(user.getEmail());
            final String domain = getCookieDomain();
            final ResponseCookie responseCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(isHttps())
                    .path("/")
                    .domain(domain)
                    .sameSite(isHttps() ? "None" : "Lax") // TODO Set SameSite attribute in production
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

        final String email = jwtUtils.getUsernameFromToken(token);
        final User user = userService.findByEmail(email);
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

        final String email = jwtUtils.getUsernameFromToken(token);
        final User existingUser = userService.findByEmail(email);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (user.getEmail() != null) {
            if (!existingUser.getEmail().equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
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
        userService.updateUser(email, existingUser);
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

        final String email = jwtUtils.getUsernameFromToken(token);
        final User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userService.deleteUser(email);
        // Invalidate the cookie by setting it to expire
        final String domain = getCookieDomain();
        final ResponseCookie responseCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(isHttps())
                .path("/")
                .domain(domain)
                .sameSite(isHttps() ? "None" : "Lax") // More compatible across browsers
                .maxAge(0) // Set cookie expiration time to 0 to delete it
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body("User deleted successfully");
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signoutUser() {
        final String domain = getCookieDomain();
        final ResponseCookie responseCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(isHttps())
                .path("/")
                .domain(domain)
                .sameSite(isHttps() ? "None" : "Lax") // More compatible across browsers
                .maxAge(0) // Set cookie expiration time to 0 to delete it
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body("User logged out successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        final String email = authentication.getName();
        final User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user.getId().toString());
    }

}