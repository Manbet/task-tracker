package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.services.JobService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PutMapping("/jobs/flush")
    public void flushJobs() {
        jobService.manuallyFlushExpiredTasks();
    }

    @PutMapping("/jobs/deadlines")
    public void deadlinesJobs() {
        jobService.manuallyNotifyDeadlines();
    }
}
