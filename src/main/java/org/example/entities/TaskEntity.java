package org.example.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    @NotBlank(message = "Title must not be blank")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;

    @Column(name = "due_time")
    private LocalDateTime dueTime;

    @Column(name = "comment")
    private String comment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "reporter", referencedColumnName = "id")
    private UserEntity reporter;

    @ManyToOne
    @JoinColumn(name = "assignee", referencedColumnName = "id")
    private UserEntity assignee;

    @ManyToOne
    @JoinColumn(name = "project", referencedColumnName = "id")
    private ProjectEntity project;

    @ManyToMany
    @JoinTable(
            name = "task_watchers",
            joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<UserEntity> watchers = new ArrayList<>();
}
