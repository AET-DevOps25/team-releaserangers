package devops25.releaserangers.coursemgmt_service.repository;

import devops25.releaserangers.coursemgmt_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
}