package devops25.releaserangers.authentication_service.controller;

import devops25.releaserangers.authentication_service.model.User;
import devops25.releaserangers.authentication_service.security.JwtUtil;
import devops25.releaserangers.authentication_service.service.UserService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
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

    private final MeterRegistry authRegistry;
    private final Counter authServiceRequestCounter;
    private final Counter authErrorTotal;
    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "Gauge is used to track latency")
    private Gauge authLatencyGauge;

    @Getter
    @Setter
    private volatile double currentLatency = 0.0;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Exposing service references is acceptable here")
    public AuthController(MeterRegistry authRegistry) {

        this.authRegistry = authRegistry;
        authRegistry.config().commonTags("service", "auth-service");

        this.authServiceRequestCounter =  Counter.builder("auth_service_requests_total")
                .description("Total number of authentication requests")
                .tags("service", "auth-service")
                .register(authRegistry);
        this.authErrorTotal = Counter.builder("auth_service_errors_total")
                .description("Total number of errors in authentication service")
                .tags("service", "auth-service")
                .register(authRegistry);
        this.authLatencyGauge = Gauge.builder("auth_service_current_latency", this, AuthController::getCurrentLatency)
                .description("Current latency of the latest authentication request")
                .tags("service", "auth-service")
                .register(authRegistry);
    }

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

    private ResponseCookie createResponseCookie(String token, int maxAge, Boolean secure) {
        final ResponseCookie.ResponseCookieBuilder responseCookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(secure ? "None" : "Lax")
                .maxAge(maxAge);
        if (secure) {
            responseCookie.domain(getCookieDomain());
        }
        return responseCookie.build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        authServiceRequestCounter.increment();
        final long startTime = System.nanoTime();
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            final String token = jwtUtils.generateToken(user.getEmail());
            final User authenticatedUser = userService.findByEmail(user.getEmail());
            final ResponseCookie responseCookie = createResponseCookie(token, 3600, isHttps());
            updateLatency(startTime);
            return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body(authenticatedUser);
        } catch (BadCredentialsException e) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        authServiceRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (userService.existsByEmail(user.getEmail())) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        final User newUser = new User(
            null,
            user.getEmail(),
            user.getName(),
            encoder.encode(user.getPassword()),
            null,
            null
        );
        userService.registerUser(newUser);
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            final String token = jwtUtils.generateToken(user.getEmail());
            final ResponseCookie responseCookie = createResponseCookie(token, 3600, isHttps());
            updateLatency(startTime);
            return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body(newUser);
        } catch (BadCredentialsException e) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserDetails(@CookieValue(value = "token", required = false) String token) {
        authServiceRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (!jwtUtils.validateJwtToken(token)) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        final String email = jwtUtils.getUsernameFromToken(token);
        final User user = userService.findByEmail(email);
        if (user == null) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        updateLatency(startTime);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUserDetails(@RequestBody User user, @CookieValue(value = "token", required = false) String token) {
        authServiceRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!jwtUtils.validateJwtToken(token)) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        final String email = jwtUtils.getUsernameFromToken(token);
        final User existingUser = userService.findByEmail(email);
        if (existingUser == null) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (user.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) {
            if (userService.existsByEmail(user.getEmail())) {
                authErrorTotal.increment();
                updateLatency(startTime);
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
        updateLatency(startTime);
        return ResponseEntity.ok(existingUser);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@CookieValue(value = "token", required = false) String token) {
        authServiceRequestCounter.increment();
        final long startTime = System.nanoTime();
        if (token == null) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!jwtUtils.validateJwtToken(token)) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        final String email = jwtUtils.getUsernameFromToken(token);
        final User user = userService.findByEmail(email);
        if (user == null) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userService.deleteUser(email);
        final ResponseCookie responseCookie = createResponseCookie("", 0, isHttps());
        updateLatency(startTime);
        return ResponseEntity.ok()
            .header("Set-Cookie", responseCookie.toString())
            .body("User deleted successfully");
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signoutUser() {
        authServiceRequestCounter.increment();
        final long startTime = System.nanoTime();
        final ResponseCookie responseCookie = createResponseCookie("", 0, isHttps());
        updateLatency(startTime);
        return ResponseEntity.ok()
            .header("Set-Cookie", responseCookie.toString())
            .body("User logged out successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        authServiceRequestCounter.increment();
        final long startTime = System.nanoTime();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        final String email = authentication.getName();
        final User user = userService.findByEmail(email);
        if (user == null) {
            authErrorTotal.increment();
            updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        updateLatency(startTime);
        return ResponseEntity.ok(user.getId().toString());
    }

    private void updateLatency(long startTime) {
        final long latency = System.nanoTime() - startTime;
        setCurrentLatency(latency / 1_000_000.0);
    }
}
