package com.strayrescue.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "animal_images")
public class AnimalImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "file_name")
    private String fileName; // Original filename from upload
    
    @NotBlank
    @Column(name = "s3_key")
    private String s3Key; // Unique key in S3 bucket
    
    @NotBlank
    @Column(name = "s3_url")
    private String s3Url; // Full URL to access the image
    
    @Column(name = "file_size")
    private Long fileSize; // Size in bytes
    
    @Column(name = "content_type")
    private String contentType; // "image/jpeg", "image/png", etc.
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false; // Main photo for the animal
    
    @Column(name = "description")
    private String description; // Optional caption
    
    @Column(name = "display_order")
    private Integer displayOrder = 0; // Order to show photos
    
    // Relationship to Animal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    @JsonBackReference("animal-images")
    @NotNull
    private Animal animal;
    
    // Relationship to User who uploaded
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "reportedAnimals", "careAnimals", "uploadedImages"})
    private User uploadedBy;
    
    // Timestamps
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public AnimalImage() {}
    
    public AnimalImage(String fileName, String s3Key, String s3Url, Animal animal, User uploadedBy) {
        this.fileName = fileName;
        this.s3Key = s3Key;
        this.s3Url = s3Url;
        this.animal = animal;
        this.uploadedBy = uploadedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
    
    public String getS3Url() { return s3Url; }
    public void setS3Url(String s3Url) { this.s3Url = s3Url; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}