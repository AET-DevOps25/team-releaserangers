package devops25.releaserangers.upload_service.repository;

import devops25.releaserangers.upload_service.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, String> {
}

