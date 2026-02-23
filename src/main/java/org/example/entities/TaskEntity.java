package org.example.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id")
    @SequenceGenerator(name = "id", sequenceName = "id_seq", allocationSize = 1)
    public long id;

    @Column(name = "title")
    public String title;

    @Column(name = "description")
    public String description;

    @Column(name = "creation_time")
    public LocalDateTime creationTime;

    @Column(name = "last_update_time")
    public LocalDateTime lastUpdateTime;

    @Column(name = "due_time")
    public LocalDateTime dueTime;

    @Column(name = "comment")
    public String comment;

//    @Column(name = "executor")
//    private UserEntity executor;
//
//    @Column(name = "creator")
//    private final UserEntity creator;

}
