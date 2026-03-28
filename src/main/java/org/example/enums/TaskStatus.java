package org.example.enums;

public enum TaskStatus {
    IDLE("idle"),
    PROCESSING ("processing"),
    CANCELLED ("cancelled"),
    ERROR ("error"),
    COMPLETED ("completed");

    public final String status;

    TaskStatus(String status) {
        this.status = status;
    }
}
