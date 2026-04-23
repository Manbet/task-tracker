package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.jobs.TaskJobs;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {
    private final TaskJobs taskJobs;

    public void manuallyNotifyDeadlines() {
        taskJobs.notifyDeadlines();
    }

    public void manuallyFlushExpiredTasks() {
        taskJobs.flushExpiredTasks();
    }
}
