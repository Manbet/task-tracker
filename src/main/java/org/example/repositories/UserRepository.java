package org.example.repositories;

import org.example.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @NativeQuery(value = "select username from users where username = ?1")
    UserEntity findByUsername(String username);
}
