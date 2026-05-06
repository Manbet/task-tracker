package org.example.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class CommentEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;

    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    private UserEntity author;

    @ManyToOne
    @JoinColumn(name = "task", referencedColumnName = "id")
    private TaskEntity task;

    public CommentEntity(UserEntity user, String text, TaskEntity task, LocalDateTime creationTime, LocalDateTime lastUpdateTime) {
        this.author = user;
        this.text = text;
        this.task = task;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
    }
}
