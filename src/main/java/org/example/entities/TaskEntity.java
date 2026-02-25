package org.example.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

//    @Column(name = "executor")
//    private UserEntity executor;
//
//    @Column(name = "creator")
//    private final UserEntity creator;

}
