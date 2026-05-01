package org.example.repositories;

import org.example.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = "select * from comments where text = ?1", nativeQuery = true)
    CommentEntity findCommentByText(String text);
}
