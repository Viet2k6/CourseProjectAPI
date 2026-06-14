package com.example.courseprojectapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseCode;

    private String courseName;

    private Integer credit;

    @ManyToMany(mappedBy = "courses")
    private List<User> students;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private User lecturer;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Material> materials;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Submission> submissions;
}
