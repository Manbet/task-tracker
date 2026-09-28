package org.example.mappers;

import org.example.dto.responses.UserResponse;
import org.example.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {TaskMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "watchedTasks", target = "waitingTasks")
    UserResponse toDto(UserEntity entity);

    List<UserResponse> toDtoList(List<UserEntity> entities);
}