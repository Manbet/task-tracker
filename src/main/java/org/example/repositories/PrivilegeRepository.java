package org.example.repositories;

import org.example.entities.PrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    @NativeQuery(value = "select * from privileges where name = ?1")
    PrivilegeEntity findByName(String name);
}
