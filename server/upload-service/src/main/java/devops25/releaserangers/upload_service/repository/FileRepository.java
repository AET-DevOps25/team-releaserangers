package devops25.releaserangers.upload_service.repository;

import devops25.releaserangers.upload_service.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, String> {
    List<File> findByCourseId(String courseId);

    @Query("SELECT new File(f.id, f.filename, f.contentType, null, f.courseId, f.createdAt, f.updatedAt) FROM File f WHERE f.courseId = :courseId")
    List<File> findByCourseIdWithoutData(@Param("courseId") String courseId);

    @Query("SELECT new File(f.id, f.filename, f.contentType, f.data, f.courseId, f.createdAt, f.updatedAt) FROM File f WHERE f.filename = :filename")
    File findByFilename(@Param("filename") String filename);

    @Modifying
    @Query("UPDATE File f SET f.filename = :fileName, f.contentType = :contentType, f.data = :data, f.updatedAt = CURRENT_TIMESTAMP WHERE f.id = :id")
    void updateFile(@Param("id") String id,
                              @Param("fileName") String fileName,
                              @Param("contentType") String contentType,
                              @Param("data") byte[] data,
                              @Param("courseId") String courseId);
}
