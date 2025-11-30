package com.example.fatecCarCarona.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.CourseDTO;
import com.example.fatecCarCarona.service.CourseService;

@RestController
@RequestMapping("/courses")
public class CourseController {
	@Autowired
	CourseService courseService;
	@GetMapping
	public ResponseEntity<List<CourseDTO> > allCourses(){
		List<CourseDTO> allCourses = courseService.allCourses();
		return ResponseEntity.ok(allCourses);

	}

}
