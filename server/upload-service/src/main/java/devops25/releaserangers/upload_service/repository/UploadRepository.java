package devops25.releaserangers.upload_service.repository;

import devops25.releaserangers.upload_service.model.Upload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadRepository extends JpaRepository<Upload, Long> {
}

