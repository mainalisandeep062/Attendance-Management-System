package com.texas.developers.ams.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;



@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "mobile_number", nullable = false, unique=true, length = 10)
    private String mobileNumber;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name="faculty", nullable= false)
    private String faculty;

    @Column(name="college_join_date", length=10)
    private Date collegeJoinDate;
}
