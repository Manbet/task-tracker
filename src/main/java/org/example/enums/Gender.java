package org.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE("М"),
    FEMALE("Ж"),
    SYSTEM("С");

    private final String name;
}
