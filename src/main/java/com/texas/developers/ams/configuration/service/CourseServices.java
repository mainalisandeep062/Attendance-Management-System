package com.texas.developers.ams.configuration.service;

import com.texas.developers.ams.entity.Course;
import com.texas.developers.ams.exception.CourseAlreasyExistException;
import com.texas.developers.ams.repo.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseServices {

//    private final CourseRepository courseRepository;
    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public void saveCourse(Course course) {
        Optional<Course> exists = courseRepository.findCourseByCourseName(course.getCourseName());

        if(exists.isPresent()){
            throw new CourseAlreasyExistException("course_already_exists");
        }

        courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
