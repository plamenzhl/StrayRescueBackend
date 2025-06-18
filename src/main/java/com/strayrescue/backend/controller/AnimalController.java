package com.strayrescue.backend.controller;

import com.strayrescue.backend.dto.response.AnimalDto;
import com.strayrescue.backend.mapper.AnimalMapper;
import com.strayrescue.backend.model.Animal;
import com.strayrescue.backend.model.AnimalStatus;
import com.strayrescue.backend.model.User;
import com.strayrescue.backend.service.AnimalService;
import com.strayrescue.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/animals")
@CrossOrigin(origins = "*")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnimalMapper animalMapper;

    // Report a new animal
    @PostMapping
    public ResponseEntity<?> reportAnimal(@RequestBody AnimalReportRequest request) {
        try {
            // Find the user who's reporting
            Optional<User> userOptional = userService.getUserById(request.getReportedByUserId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            Animal animal = animalService.reportAnimal(
                request.getName(),
                request.getSpecies(),
                request.getDescription(),
                request.getLatitude(),
                request.getLongitude(),
                request.getLocationDescription(),
                request.getStatus() != null ? request.getStatus() : AnimalStatus.REPORTED,
                userOptional.get()
            );
            // Return DTO instead of raw entity
            AnimalDto animalDto = animalMapper.toDto(animal);
            return ResponseEntity.ok(animalDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all animals
    @GetMapping
    public ResponseEntity<List<AnimalDto>> getAllAnimals() {
        List<Animal> animals = animalService.getAllAnimals();
        List<AnimalDto> animalDtos = animalMapper.toDtoList(animals);
        return ResponseEntity.ok(animalDtos);
    }

    // Get animal by ID
    @GetMapping("/{id}")
    public ResponseEntity<AnimalDto> getAnimalById(@PathVariable Long id) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        return animal.map(a -> ResponseEntity.ok(animalMapper.toDto(a)))
                    .orElse(ResponseEntity.notFound().build());
    }

    // Get animals by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AnimalDto>> getAnimalsByStatus(@PathVariable AnimalStatus status) {
        List<Animal> animals = animalService.getAnimalsByStatus(status);
        List<AnimalDto> animalDtos = animalMapper.toDtoList(animals);
        return ResponseEntity.ok(animalDtos);
    }

    // Search animals by species
    @GetMapping("/search/species")
    public ResponseEntity<List<AnimalDto>> searchBySpecies(@RequestParam String species) {
        List<Animal> animals = animalService.searchBySpecies(species);
        List<AnimalDto> animalDtos = animalMapper.toDtoList(animals);
        return ResponseEntity.ok(animalDtos);
    }

    // Find animals near location
    @GetMapping("/near")
    public ResponseEntity<List<AnimalDto>> findAnimalsNear(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        List<Animal> animals = animalService.findAnimalsNearLocation(latitude, longitude, radiusKm);
        List<AnimalDto> animalDtos = animalMapper.toDtoList(animals);
        return ResponseEntity.ok(animalDtos);
    }

    // Search animals
    @GetMapping("/search")
    public ResponseEntity<List<AnimalDto>> searchAnimals(@RequestParam String q) {
        List<Animal> animals = animalService.searchAnimals(q);
        List<AnimalDto> animalDtos = animalMapper.toDtoList(animals);
        return ResponseEntity.ok(animalDtos);
    }

    // Update animal status - FIXED TO USE DTOs
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateAnimalStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            Animal updatedAnimal = animalService.updateAnimalStatus(id, request.getStatus());
            AnimalDto animalDto = animalMapper.toDto(updatedAnimal);
            return ResponseEntity.ok(animalDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete animal
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnimal(@PathVariable Long id) {
        try {
            animalService.deleteAnimal(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get recent animals
    @GetMapping("/recent")
    public ResponseEntity<List<AnimalDto>> getRecentAnimals() {
        List<Animal> animals = animalService.getRecentAnimals();
        List<AnimalDto> animalDtos = animalMapper.toDtoList(animals);
        return ResponseEntity.ok(animalDtos);
    }

    // Request classes
    public static class AnimalReportRequest {
        private String name;
        private String species;
        private String description;
        private Double latitude;
        private Double longitude;
        private String locationDescription;
        private AnimalStatus status;
        private Long reportedByUserId;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getSpecies() { return species; }
        public void setSpecies(String species) { this.species = species; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        
        public String getLocationDescription() { return locationDescription; }
        public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
        
        public AnimalStatus getStatus() { return status; }
        public void setStatus(AnimalStatus status) { this.status = status; }
        
        public Long getReportedByUserId() { return reportedByUserId; }
        public void setReportedByUserId(Long reportedByUserId) { this.reportedByUserId = reportedByUserId; }
    }

    public static class StatusUpdateRequest {
        private AnimalStatus status;

        public AnimalStatus getStatus() { return status; }
        public void setStatus(AnimalStatus status) { this.status = status; }
    }
}