package org.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    @OneToMany(mappedBy = "reporter")
    private List<TaskEntity> reportedTasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignee")
    private List<TaskEntity> assignedTasks = new ArrayList<>();

    @ManyToMany(mappedBy = "watchers")
    private List<TaskEntity> watchedTasks = new ArrayList<>();
}
