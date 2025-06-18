package com.strayrescue.backend.mapper;

import com.strayrescue.backend.dto.response.AnimalImageDto;
import com.strayrescue.backend.model.AnimalImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnimalImageMapper {
    
    @Mapping(source = "animal.id", target = "animalId")
    @Mapping(source = "uploadedBy.username", target = "uploadedByUsername")
    AnimalImageDto toDto(AnimalImage animalImage);
    
    List<AnimalImageDto> toDtoList(List<AnimalImage> animalImages);
}