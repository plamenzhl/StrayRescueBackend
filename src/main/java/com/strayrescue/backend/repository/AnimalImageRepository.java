package com.strayrescue.backend.repository;

import com.strayrescue.backend.model.AnimalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalImageRepository extends JpaRepository<AnimalImage, Long> {

    List<AnimalImage> findByAnimalIdOrderByDisplayOrder(Long animalId);
    List<AnimalImage> findByAnimalId(Long animalId);
    
    // Find all images for a specific animal
    List<AnimalImage> findByAnimalIdOrderByDisplayOrderAsc(Long animalId);
    
    // Find primary image for an animal
    Optional<AnimalImage> findByAnimalIdAndIsPrimaryTrue(Long animalId);
    
    // Find images by S3 key
    Optional<AnimalImage> findByS3Key(String s3Key);
    
    // Find images uploaded by a specific user
    List<AnimalImage> findByUploadedByIdOrderByUploadedAtDesc(Long userId);
    
    // Get total file size for an animal's images
    @Query("SELECT SUM(ai.fileSize) FROM AnimalImage ai WHERE ai.animal.id = :animalId")
    Long getTotalFileSizeForAnimal(Long animalId);
    
    // Delete all images for an animal
    void deleteByAnimalId(Long animalId);
    
    // Count images for an animal
    long countByAnimalId(Long animalId);
}