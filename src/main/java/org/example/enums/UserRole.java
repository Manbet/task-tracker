package org.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN(Set.of(Permission.READ_DATA, Permission.WRITE_DATA,Permission.DELETE_DATA)),
    USER(Set.of(Permission.READ_DATA));

    private final Set<Permission> permissions;
}
