package org.example.repositories;

import org.example.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @NativeQuery(value = "select * from tasks where (due_time - current_timestamp) < interval '7 days'" +
            "or (due_time - current_timestamp) < 0.1 * (due_time - creation_time)")
    List<TaskEntity> findDeadlines();

    @Modifying
    @NativeQuery(value = "delete from tasks where (current_timestamp - due_time) > interval '1 year'")
    int findExpiredTasks();

    @NativeQuery(value = "select * " +
            "from tasks, projects, project_users " +
            "where tasks.id = ?1 and (projects.id = tasks.project and " +
            "(projects.is_open or project_users.user_id = (select username from users where username = ?2)))")
    TaskEntity findAccessibleByUsername(long taskId, String username);

    @NativeQuery(value = "select * " +
            "from tasks, projects, project_users " +
            "where projects.id = tasks.project and " +
            "(projects.is_open or project_users.user_id = (select username from users where username = ?1))")
    List<TaskEntity> findAllOpen(String username);
}
