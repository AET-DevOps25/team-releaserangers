package devops25.releaserangers.coursemgmt_service.service;

import devops25.releaserangers.coursemgmt_service.model.Course;
import devops25.releaserangers.coursemgmt_service.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    public List<Course> getCoursesByUserId(String userId) {
        return courseRepository.findAll().stream()
                .filter(course -> course.getUserId().equals(userId)).toList();
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Course course) {
        Course existingCourse = getCourseById(course.getId());
        if (existingCourse != null) {
            courseRepository.delete(existingCourse);
        }
    }
}