package devops25.releaserangers.upload_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UploadServiceApplication {

    static {
        Dotenv dotenv;
        try {
            dotenv = Dotenv.configure().filename(".env.prod").ignoreIfMissing().load();

            dotenv = Dotenv.configure().directory("../../.").filename(".env.prod").ignoreIfMissing().load();

            if (dotenv.get("CLIENT_URL") == null) {
                System.out.println("CLIENT_URL not found in .env.prod, falling back to .env in the parent directory");
                dotenv = Dotenv.configure().directory("../../.").load();
            }
        } catch (Exception e) {
            System.out.println("Failed to load .env.prod and .env, falling back to .env");
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        }
        final String clientUrl = dotenv.get("CLIENT_URL");
        if (clientUrl != null) {
            System.setProperty("CLIENT_URL", clientUrl);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(UploadServiceApplication.class, args);
    }
}
