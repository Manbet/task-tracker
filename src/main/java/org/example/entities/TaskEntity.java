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
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "creationTime")
    public LocalDateTime creationTime;

    @Column(name = "lastUpdateTime")
    public LocalDateTime lastUpdateTime;

    @Column(name = "dueDate")
    public LocalDateTime dueDate;

    @Column(name = "comment")
    public String comment;

//    @Column(name = "executor")
//    private UserEntity executor;
//
//    @Column(name = "creator")
//    private final UserEntity creator;

}
