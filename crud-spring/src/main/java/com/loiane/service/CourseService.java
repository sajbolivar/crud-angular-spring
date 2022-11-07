package com.loiane.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.loiane.dto.CourseDTO;
import com.loiane.dto.CourseRequestDTO;
import com.loiane.dto.mapper.CourseMapper;
import com.loiane.enums.Status;
import com.loiane.exception.BusinessException;
import com.loiane.exception.RecordNotFoundException;
import com.loiane.model.Course;
import com.loiane.repository.CourseRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Service
@Validated
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public List<CourseDTO> findAll(int page, int pageSize) {
        return courseRepository.findByStatus(PageRequest.of(page, pageSize), Status.ACTIVE)
                .stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO findById(@Positive @NotNull Long id) {
        return courseRepository.findByIdAndStatus(id, Status.ACTIVE).map(courseMapper::toDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public CourseDTO create(@Valid CourseRequestDTO courseRequestDTO) {

        courseRepository.findByName(courseRequestDTO.name()).stream()
                .filter(c -> c.getStatus().equals(Status.ACTIVE))
                .findAny().ifPresent(c -> {
                    throw new BusinessException("A course with name " + courseRequestDTO.name() + " already exists.");
                });

        Course course = courseMapper.toModel(courseRequestDTO);
        course.setStatus(Status.ACTIVE);
        return courseMapper.toDTO(courseRepository.save(course));
    }

    public CourseDTO update(@Positive @NotNull Long id, @Valid CourseRequestDTO courseRequestDTO) {
        return courseRepository.findByIdAndStatus(id, Status.ACTIVE).map(actual -> {
            actual.setName(courseRequestDTO.name());
            actual.setCategory(courseMapper.convertCategoryValue(courseRequestDTO.category()));
            return courseMapper.toDTO(courseRepository.save(actual));
        })
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public void delete(@Positive @NotNull Long id) {
        courseRepository.delete(courseRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new RecordNotFoundException(id)));
    }
}
