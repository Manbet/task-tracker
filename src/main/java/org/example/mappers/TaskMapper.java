package org.example.mappers;

import org.example.dto.responses.TaskResponse;
import org.example.entities.TaskEntity;
import org.example.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "reporter.id", target = "reporterId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "watchers", target = "watcherIds")
    TaskResponse toDto(TaskEntity entity);

    List<TaskResponse> toDtoList(List<TaskEntity> entities);

    default Long mapUserToId(UserEntity user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}
