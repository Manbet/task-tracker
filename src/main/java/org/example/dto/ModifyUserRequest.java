package org.example.dto;

import lombok.Data;

@Data
public class ModifyUserRequest {
    private final long id;
    private final String username;
}
