package org.example.repositories;

import org.example.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query(value = "select * from tasks where (current_timestamp - due_time) < interval '1 year'", nativeQuery = true)
    List<TaskEntity> findExpiredTasks();
}
