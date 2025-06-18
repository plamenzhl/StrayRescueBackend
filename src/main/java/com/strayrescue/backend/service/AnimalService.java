package com.strayrescue.backend.service;

import com.strayrescue.backend.model.Animal;
import com.strayrescue.backend.model.AnimalStatus;
import com.strayrescue.backend.model.User;
import com.strayrescue.backend.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    // Report a new animal
    public Animal reportAnimal(String name, String species, String description,
                              Double latitude, Double longitude, String locationDescription,
                              AnimalStatus status, User reportedBy) {
        
        Animal animal = new Animal();
        animal.setName(name);
        animal.setSpecies(species);
        animal.setDescription(description);

        // Convert Double to BigDecimal
        animal.setLatitude(latitude != null ? BigDecimal.valueOf(latitude) : null);
        animal.setLongitude(longitude != null ? BigDecimal.valueOf(longitude) : null);

        animal.setLocationDescription(locationDescription);
        animal.setStatus(status);
        animal.setReportedBy(reportedBy);

        return animalRepository.save(animal);
    }

    // Get all animals
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    // Get animal by ID
    public Optional<Animal> getAnimalById(Long id) {
        return animalRepository.findById(id);
    }

    // Get animals by status
    public List<Animal> getAnimalsByStatus(AnimalStatus status) {
        return animalRepository.findByStatus(status);
    }

    // Search animals by species
    public List<Animal> searchBySpecies(String species) {
        return animalRepository.findBySpeciesContainingIgnoreCase(species);
    }

    // Find animals near location
    public List<Animal> findAnimalsNearLocation(Double latitude, Double longitude, Double radiusKm) {
        return animalRepository.findAnimalsWithinRadius(latitude, longitude, radiusKm);
    }

    // Get animals reported by user
    public List<Animal> getAnimalsByUser(Long userId) {
        return animalRepository.findByReportedByIdOrderByCreatedAtDesc(userId);
    }

    // Search animals
    public List<Animal> searchAnimals(String searchTerm) {
        return animalRepository.searchAnimals(searchTerm);
    }

    // Update animal status
    public Animal updateAnimalStatus(Long id, AnimalStatus newStatus) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        
        animal.setStatus(newStatus);
        animal.setUpdatedAt(LocalDateTime.now());
        
        return animalRepository.save(animal);
    }

    // Update animal details
    public Animal updateAnimal(Long id, String name, String species, String description, String locationDescription) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        
        animal.setName(name);
        animal.setSpecies(species);
        animal.setDescription(description);
        animal.setLocationDescription(locationDescription);
        animal.setUpdatedAt(LocalDateTime.now());
        
        return animalRepository.save(animal);
    }

    // Delete animal
    public void deleteAnimal(Long id) {
        animalRepository.deleteById(id);
    }

    // Get recent animals (last 24 hours)
    public List<Animal> getRecentAnimals() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        return animalRepository.findByCreatedAtAfterOrderByCreatedAtDesc(yesterday);
    }

    // Get animal statistics
    public long countAnimalsByStatus(AnimalStatus status) {
        return animalRepository.countByStatus(status);
    }
}