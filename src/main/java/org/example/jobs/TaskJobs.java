package org.example.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.TaskEntity;
import org.example.repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskJobs {
    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void notifyDeadlines() {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Notifying deadlines", uuid);
        List<TaskEntity> dueTasks = taskRepository.findAll();
        for (TaskEntity task : dueTasks) {
            long duration = Duration.between(LocalDateTime.now(), task.getDueTime()).toDays();
            if (duration <= (Duration.between(task.getCreationTime(),
                    task.getDueTime()).toDays() * 0.1) || duration <= 7) {
                task.getComments().add(duration + " Days till deadline");
                taskRepository.save(task);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void flushExpiredTasks() {
        String uuid = UUID.randomUUID().toString();
        log.info("[{}] Trying to flush expired tasks", uuid);
        int size = taskRepository.findExpiredTasks();
        log.info("[{}] Successfully deleted {} expired tasks", uuid, size);
    }
}
