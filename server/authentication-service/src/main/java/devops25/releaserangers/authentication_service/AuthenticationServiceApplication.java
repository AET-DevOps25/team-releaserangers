package devops25.releaserangers.authentication_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticationServiceApplication {

  static {
    Dotenv dotenv;
    try {
      dotenv = Dotenv.configure().filename(".env.prod").ignoreIfMissing().load();
      System.out.println("Trying default dir: " + dotenv.get("JWT_SECRET"));

      dotenv = Dotenv.configure().directory("../../.").filename(".env.prod").ignoreIfMissing().load();
      System.out.println("Trying other path: " + dotenv.get("JWT_SECRET"));

      if (dotenv.get("JWT_SECRET") == null) {
        System.out.println("JWT_SECRET not found in .env.prod, falling back to .env in the parent directory");
        dotenv = Dotenv.configure().directory("../../.").load();
      }
    } catch (Exception e) {
        System.out.println("Failed to load .env.prod and .env, falling back to .env");
      dotenv = Dotenv.configure().ignoreIfMissing().load();
    }
    final String jwtSecret = dotenv.get("JWT_SECRET");
    if (jwtSecret != null) {
      System.setProperty("JWT_SECRET", jwtSecret);
    }
    final String clientUrl = dotenv.get("CLIENT_URL");
    if (clientUrl != null) {
      System.setProperty("CLIENT_URL", clientUrl);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(AuthenticationServiceApplication.class, args);
  }
}
