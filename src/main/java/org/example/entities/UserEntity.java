package org.example.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.Gender;

import java.time.LocalDateTime;
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
    private LocalDateTime birthDate;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "reporter")
    private List<TaskEntity> reportedTasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignee")
    private List<TaskEntity> assignedTasks = new ArrayList<>();

    @ManyToMany(mappedBy = "watchers")
    private List<TaskEntity> watchedTasks = new ArrayList<>();

    @ManyToMany(mappedBy = "users")
    private List<ProjectEntity> projects = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
}
