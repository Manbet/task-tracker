package org.example.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.CommentEntity;
import org.example.entities.TaskEntity;
import org.example.repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskJobs {
    private final TaskRepository taskRepository;

    @Scheduled(cron = "${app.jobs.notify-deadlines.cron}")
    public void notifyDeadlines() {
        log.info("Notifying deadlines");
        taskRepository.findDeadlines();
        List<TaskEntity> dueTasks = taskRepository.findDeadlines();
        for (TaskEntity task : dueTasks) {
            long duration = Duration.between(LocalDateTime.now(), task.getDueTime()).toDays();
            CommentEntity comment = new CommentEntity(duration + " Days till deadline", task,
                    LocalDateTime.now(), LocalDateTime.now());
            task.getComments().add(comment);
            taskRepository.save(task);
        }
    }

    @Scheduled(cron = "${app.jobs.flush-expired-tasks.cron}")
    public void flushExpiredTasks() {
        log.info("Trying to flush expired tasks");
        int size = taskRepository.findExpiredTasks();
        log.info("Successfully deleted {} expired tasks", size);
    }
}
