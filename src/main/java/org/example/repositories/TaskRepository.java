package org.example.repositories;

import org.example.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Modifying
    @Query(value = "delete from tasks where (current_timestamp - due_time) > interval '1 year'", nativeQuery = true)
    int findExpiredTasks();
}
