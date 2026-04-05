package org.example.repositories;

import org.example.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @Query(value = "select * from projects where name = ?1", nativeQuery = true)
    ProjectEntity findByName(String name);
}
