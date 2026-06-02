package org.example.repositories;

import org.example.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @NativeQuery(value = "select * from projects where name = ?1")
    ProjectEntity findByName(String name);

    @NativeQuery(value = "select * " +
            "from projects, project_users " +
            "where projects.is_open = true " +
            "or project_users.user_id = (select id from users where username = ?1)")
    List<ProjectEntity> findAllOpen(String username);

    @NativeQuery(value = "select is_open or pu.user_id is not null " +
            "from projects p " +
            "left join project_users pu on p.id = pu.project_id and pu.user_id = ?2 where p.id = ?1")
    boolean isAccessible(long projectId, long userId);
}
