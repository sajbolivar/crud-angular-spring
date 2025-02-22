package com.loiane.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;

import com.loiane.enums.Category;
import com.loiane.enums.Status;
import com.loiane.enums.converters.CategoryConverter;
import com.loiane.enums.converters.StatusConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@SQLDelete(sql = "UPDATE Course SET status = 'Inactive' WHERE id=?")
@Where(clause = "status <> 'Inactive'")
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @JsonProperty("_id")
    private Long id;

    @NotBlank
    @NotNull
    @Length(min = 5, max = 200)
    @Column(length = 200, nullable = false)
    private String name;

    @NotNull
    @Column(length = 10, nullable = false)
    @Convert(converter = CategoryConverter.class)
    private Category category;

    @NotNull
    @Column(length = 8, nullable = false)
    @Convert(converter = StatusConverter.class)
    private Status status;

    // @NotNull
    // @NotEmpty
    @Valid
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    // @OneToMany(cascade = CascadeType.ALL)
    // @JoinColumn(name = "courseID", referencedColumnName = "id")
    private Set<Lesson> lessons = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        if (lessons == null) {
            throw new IllegalArgumentException("Lessons cannot be null.");
        }
        this.lessons = lessons;
    }

}
