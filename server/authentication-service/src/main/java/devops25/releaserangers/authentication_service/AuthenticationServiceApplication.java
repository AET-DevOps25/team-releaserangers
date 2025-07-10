package devops25.releaserangers.authentication_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticationServiceApplication {

  static {
    // Load .env variables into environment before Spring starts
    Dotenv.configure().ignoreIfMissing().load();
  }

  public static void main(String[] args) {
    SpringApplication.run(AuthenticationServiceApplication.class, args);
  }
}
