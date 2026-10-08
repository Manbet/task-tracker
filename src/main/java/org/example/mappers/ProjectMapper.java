package org.example.mappers;

import org.example.dto.responses.ProjectResponse;
import org.example.entities.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {TaskMapper.class, UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectResponse toDto(ProjectEntity entity);

    List<ProjectResponse> toDtoList(List<ProjectEntity> entities);
}