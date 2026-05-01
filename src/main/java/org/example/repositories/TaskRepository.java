package org.example.repositories;

import org.example.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query(value = "select * from tasks where (due_time - current_timestamp) < interval '7 days'" +
            "or (due_time - current_timestamp) < 0.1 * due_time", nativeQuery = true)
    List<TaskEntity> findDeadlines();


    @Modifying
    @Query(value = "delete from tasks where (current_timestamp - due_time) > interval '1 year'", nativeQuery = true)
    int findExpiredTasks();
}
