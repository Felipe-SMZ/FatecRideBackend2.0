package com.example.fatecCarCarona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.dto.CourseDTO;
import com.example.fatecCarCarona.entity.Course;
import com.example.fatecCarCarona.repository.CourseRepository;

@Service
public class CourseService {
	@Autowired
	CourseRepository courseRepository;

	public Course validateCourse(long id) {
			return courseRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Curso não cadastrado"));
	}

	public List<CourseDTO> allCourses(){
		List<Course> allCourses = courseRepository.findAll();
		List<CourseDTO> allCoursesDTO = allCourses.stream()
				.map(course -> new CourseDTO(course.getId(), course.getName()))
				.collect(Collectors.toList());
		return allCoursesDTO;

	}
}
