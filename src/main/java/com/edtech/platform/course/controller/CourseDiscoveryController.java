package com.edtech.platform.course.controller;

import com.edtech.platform.course.dto.CourseDetailResponse;
import com.edtech.platform.course.dto.CourseSearchRequest;
import com.edtech.platform.course.dto.CourseSummaryResponse;
import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.service.CourseDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseDiscoveryController {

    private final CourseDiscoveryService courseDiscoveryService;

    @GetMapping
    public ResponseEntity<Page<CourseSummaryResponse>> searchCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CourseDifficulty difficulty,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort) {

        CourseSearchRequest request = new CourseSearchRequest();
        request.setSearch(search);
        request.setCategory(category);
        request.setDifficulty(difficulty);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setMinRating(minRating);

        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy = "createdAt"; // default newest

        if ("popularity".equalsIgnoreCase(sort)) {
            sortBy = "studentCount";
        } else if ("price_asc".equalsIgnoreCase(sort)) {
            sortBy = "price";
            direction = Sort.Direction.ASC;
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sortBy = "price";
        } else if ("rating".equalsIgnoreCase(sort)) {
            sortBy = "rating";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(courseDiscoveryService.searchCourses(request, pageable));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(@PathVariable UUID courseId) {
        return ResponseEntity.ok(courseDiscoveryService.getCourseDetail(courseId));
    }
}
