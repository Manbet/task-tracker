package org.example.repositories;

import org.example.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

//    @Query("""
//        select distinct u
//        from UserEntity u
//        left join fetch u.reportedTasks
//        left join fetch u.assignedTasks
//        left join fetch u.watchedTasks
//        where u.id = :id
//        """)
    Optional<UserEntity> findWithTasksById(@Param("id") Long id);
}
