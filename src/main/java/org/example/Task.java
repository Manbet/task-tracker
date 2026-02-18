package org.example;

import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class Task {
    private final String name;
    private String description;
    private final LocalDateTime creationTime;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime dueDate;
    private User executor;
    private final User creator;
    private final long id;
    private String comment;

    private Task(String name, String description,
                LocalDateTime creationTime,LocalDateTime lastUpdateTime, LocalDateTime dueDate,
                User executor, User creator, long id, String comment) {
        this.name = name;
        this.description = description;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.dueDate = dueDate;
        this.executor = executor;
        this.creator = creator;
        this.id = id;
        this.comment = comment;
    }

    public void createTask(String name, String description, LocalDateTime dueDate,
                           User executor, User creator, long id, String comment) {
        new Task(name, description, LocalDateTime.now(),
                LocalDateTime.now(), dueDate, executor, creator, id, comment);
    }

    private void modifyTask(String description, LocalDateTime dueDate, User executor, String comment) {
        setDescription(description);
        setDueDate(dueDate);
        setExecutor(executor);
        setComment(comment);
        setLastUpdateTime(LocalDateTime.now());
    }


}
