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

    @NativeQuery(value = "select * from projects, project_users where projects.is_open = true and project_users.user_id = ?1")
    List<ProjectEntity> findAllOpen(long id);
}
