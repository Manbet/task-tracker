package org.example.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.CommentEntity;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.example.exceptions.NoSuchEntityException;
import org.example.repositories.TaskRepository;
import org.example.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskJobs {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "${application.jobs.notify-deadlines.cron:0 0 0 * * *}")
    public void notifyDeadlines() {
        log.info("Notifying deadlines");
        taskRepository.findDeadlines();
        List<TaskEntity> dueTasks = taskRepository.findDeadlines();
        for (TaskEntity task : dueTasks) {
            long duration = Duration.between(LocalDateTime.now(), task.getDueTime()).toDays();
            final UserEntity author = userRepository.findById(0L)
                    .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                    .format("User with id {0} not found", 0L)));
            CommentEntity comment = new CommentEntity(author, duration + " Days till deadline", task,
                    LocalDateTime.now(), LocalDateTime.now());
            task.getComments().add(comment);
            taskRepository.save(task);
        }
    }

    @Scheduled(cron = "${application.jobs.flush.cron:0 0 0 * * *}")
    public void flushExpiredTasks() {
        log.info("Trying to flush expired tasks");
        int size = taskRepository.findExpiredTasks();
        log.info("Successfully deleted {} expired tasks", size);
    }
}
