package org.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.Gender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "password")
    private String password;

    @Column(name = "email",  unique = true)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "reporter")
    private Set<TaskEntity> reportedTasks = new HashSet<>();

    @OneToMany(mappedBy = "assignee")
    private Set<TaskEntity> assignedTasks = new HashSet<>();

    @ManyToMany(mappedBy = "watchers")
    private Set<TaskEntity> watchedTasks = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private List<ProjectEntity> projects = new ArrayList<>();

//    @Column(name = "role")
//    @Enumerated(EnumType.STRING)
//    private UserRole role;
}
