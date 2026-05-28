package org.example.repositories;

import org.example.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @NativeQuery(value = "select * from users where username = ?1")
    UserEntity findByUsername(String username);
    @NativeQuery(value = "select * from users where email = ?1")
    UserEntity findByEmail(String email);
    @NativeQuery(value = "select email = ?1 from users")
    boolean emailExist(String email);
}
