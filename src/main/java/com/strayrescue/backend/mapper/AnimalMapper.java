package com.strayrescue.backend.mapper;

import com.strayrescue.backend.dto.response.AnimalDto;
import com.strayrescue.backend.model.Animal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnimalMapper {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AnimalImageMapper animalImageMapper;
    
    public AnimalDto toDto(Animal animal) {
        if (animal == null) {
            return null;
        }
        
        AnimalDto dto = new AnimalDto();
        dto.setId(animal.getId());
        dto.setName(animal.getName());
        dto.setSpecies(animal.getSpecies());
        dto.setDescription(animal.getDescription());
        dto.setLatitude(animal.getLatitude());
        dto.setLongitude(animal.getLongitude());
        dto.setLocationDescription(animal.getLocationDescription());
        dto.setStatus(animal.getStatus() != null ? animal.getStatus().name() : null);
        dto.setReportedAt(animal.getDateReported());
        dto.setLastSeenAt(animal.getLastSeenAt());
        dto.setCreatedAt(animal.getCreatedAt());
        dto.setUpdatedAt(animal.getUpdatedAt());
        
        // Map user references safely (no passwords)
        if (animal.getReportedBy() != null) {
            dto.setReportedBy(userMapper.toSummaryDto(animal.getReportedBy()));
        }
        
        if (animal.getCurrentCaretaker() != null) {
            dto.setCurrentCaretaker(userMapper.toSummaryDto(animal.getCurrentCaretaker()));
        }
        
        // Map images safely
        if (animal.getImages() != null && !animal.getImages().isEmpty()) {
            dto.setImages(animalImageMapper.toDtoList(animal.getImages().stream().collect(Collectors.toList())));
            
            // Set primary image URL
            animal.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .ifPresent(img -> dto.setPrimaryImageUrl(img.getS3Url()));
        }
        
        return dto;
    }
    
    public List<AnimalDto> toDtoList(List<Animal> animals) {
        if (animals == null) {
            return null;
        }
        
        return animals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}