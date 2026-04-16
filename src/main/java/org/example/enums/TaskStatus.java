package org.example.enums;

import org.example.exceptions.NoSuchEntityException;

import java.text.MessageFormat;
import java.util.Arrays;

public enum TaskStatus {
    IDLE ("idle"),
    PROCESSING ("processing"),
    CANCELLED ("cancelled"),
    ERROR ("error"),
    COMPLETED ("completed");

    public final String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static TaskStatus getEnumByLowercaseName(String status, String uuid) {
        return Arrays.stream(TaskStatus.values()).filter(x -> x
                .getStatus().equals(status)).findFirst()
                .orElseThrow(() -> new NoSuchEntityException(MessageFormat
                .format("[{0}] No such task status", uuid)));
    }
}
