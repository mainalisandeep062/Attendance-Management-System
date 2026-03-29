package com.texas.developers.ams.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "COURSE_NAME_REQUIRED")
    @Column(name = "course_name", length = 50, nullable = false)
    private String courseName;

    @NotBlank(message = "COURSE_DESCRIPTION_REQUIRED")
    @Column(name = "course_description", length = 1000, nullable = false)
    private String courseDescription;

}
