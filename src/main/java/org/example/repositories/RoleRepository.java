package org.example.repositories;

import org.example.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @NativeQuery(value = "select * from roles where name = ?1")
    RoleEntity findByName(String name);
}
