package com.strayrescue.backend.repository;

import com.strayrescue.backend.model.Animal;
import com.strayrescue.backend.model.AnimalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    
    // Find animals by status
    List<Animal> findByStatus(AnimalStatus status);
    
    // Find animals by species
    List<Animal> findBySpeciesContainingIgnoreCase(String species);
    
    // Find animals by location (within radius) - UPDATED for BigDecimal
    @Query("SELECT a FROM Animal a WHERE " +
           "(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(CAST(a.latitude AS double))) * " +
           "COS(RADIANS(CAST(a.longitude AS double)) - RADIANS(:lng)) + " +
           "SIN(RADIANS(:lat)) * SIN(RADIANS(CAST(a.latitude AS double))))) <= :radiusKm")
    List<Animal> findAnimalsWithinRadius(@Param("lat") Double latitude, 
                                       @Param("lng") Double longitude, 
                                       @Param("radiusKm") Double radiusKm);
    
    // Find animals reported by a specific user
    List<Animal> findByReportedByIdOrderByCreatedAtDesc(Long userId);
    
    // Find recent animals (last 24 hours)
    List<Animal> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);
    
    // Find animals by status and species
    List<Animal> findByStatusAndSpeciesContainingIgnoreCase(AnimalStatus status, String species);
    
    // Search animals by name or description
    @Query("SELECT a FROM Animal a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Animal> searchAnimals(@Param("search") String searchTerm);
    
    // Count animals by status
    long countByStatus(AnimalStatus status);
}