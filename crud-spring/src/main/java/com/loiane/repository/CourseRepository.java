package com.loiane.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loiane.enums.Status;
import com.loiane.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByStatus(Pageable pageable, Status status);

    Page<Course> findByNameAndStatus(Pageable pageable, String name, Status status);

    Optional<Course> findByIdAndStatus(Long id, Status status);

    List<Course> findByName(String name);
}
