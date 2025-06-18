package com.strayrescue.backend.controller;

import com.strayrescue.backend.dto.response.AnimalImageDto;
import com.strayrescue.backend.dto.response.ImageUploadResponse;
import com.strayrescue.backend.mapper.AnimalImageMapper;
import com.strayrescue.backend.model.AnimalImage;
import com.strayrescue.backend.model.User;
import com.strayrescue.backend.repository.UserRepository;
import com.strayrescue.backend.service.ImageUploadService;
import com.strayrescue.backend.service.AnimalService;
import com.strayrescue.backend.model.Animal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private AnimalImageMapper animalImageMapper;

    /**
     * Upload images for an animal
     * POST /api/images/animals/{animalId}
     */
    @PostMapping("/animals/{animalId}")
    public ResponseEntity<?> uploadImages(
            @PathVariable Long animalId,
            @RequestParam("files") MultipartFile[] files,
            Authentication authentication) {
        
        try {
            // Get the current user
            User currentUser = getCurrentUser(authentication);
            
            // Upload images
            List<AnimalImage> uploadedImages = imageUploadService.uploadImagesForAnimal(animalId, files, currentUser);
            
            // Convert to DTOs
            List<AnimalImageDto> imageDtos = animalImageMapper.toDtoList(uploadedImages);
            
            return ResponseEntity.ok(new ImageUploadResponse(
                "Images uploaded successfully", 
                uploadedImages.size(), 
                imageDtos
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all images for an animal
     * GET /api/images/animals/{animalId}
     */
    @GetMapping("/animals/{animalId}")
    public ResponseEntity<List<AnimalImageDto>> getAnimalImages(@PathVariable Long animalId) {
        List<AnimalImage> images = imageUploadService.getImagesForAnimal(animalId);
        List<AnimalImageDto> imageDtos = animalImageMapper.toDtoList(images);
        return ResponseEntity.ok(imageDtos);
    }

    /**
     * Get primary image for an animal
     * GET /api/images/animals/{animalId}/primary
     */
    @GetMapping("/animals/{animalId}/primary")
    public ResponseEntity<AnimalImageDto> getPrimaryImage(@PathVariable Long animalId) {
        Optional<AnimalImage> primaryImage = imageUploadService.getPrimaryImageForAnimal(animalId);
        return primaryImage.map(image -> ResponseEntity.ok(animalImageMapper.toDto(image)))
                          .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Set primary image for an animal
     * PUT /api/images/{imageId}/primary
     */
    @PutMapping("/{imageId}/primary")
    public ResponseEntity<?> setPrimaryImage(
            @PathVariable Long imageId,
            @RequestParam Long animalId,
            Authentication authentication) {
        
        try {
            AnimalImage primaryImage = imageUploadService.setPrimaryImage(animalId, imageId);
            AnimalImageDto imageDto = animalImageMapper.toDto(primaryImage);
            return ResponseEntity.ok(imageDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete an image
     * DELETE /api/images/{imageId}
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId, Authentication authentication) {
        try {
            imageUploadService.deleteImage(imageId);
            return ResponseEntity.ok(new MessageResponse("Image deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Upload a single image for an animal
     * POST /api/images/animals/{animalId}/single
     */
    @PostMapping("/animals/{animalId}/single")
    public ResponseEntity<?> uploadSingleImage(
            @PathVariable Long animalId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        try {
            User currentUser = getCurrentUser(authentication);
            
            // Get the animal first
            Animal animal = animalService.getAnimalById(animalId)
                    .orElseThrow(() -> new RuntimeException("Animal not found with id: " + animalId));
            
            AnimalImage uploadedImage = imageUploadService.uploadSingleImage(animal, file, currentUser);
            AnimalImageDto imageDto = animalImageMapper.toDto(uploadedImage);
            
            return ResponseEntity.ok(imageDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper method to get current user
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Response classes
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}