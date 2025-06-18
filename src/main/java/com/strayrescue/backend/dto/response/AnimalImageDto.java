package com.strayrescue.backend.dto.response;

import java.time.LocalDateTime;

public class AnimalImageDto {
    private Long id;
    private String fileName;
    private String s3Key;
    private String s3Url;
    private Long fileSize;
    private String contentType;
    private Boolean isPrimary;
    private Integer displayOrder;
    private String description;
    private LocalDateTime uploadedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long animalId;
    private String uploadedByUsername;
    
    public AnimalImageDto() {}
    
    // Getters and setters
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
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    
    public String getUploadedByUsername() { return uploadedByUsername; }
    public void setUploadedByUsername(String uploadedByUsername) { this.uploadedByUsername = uploadedByUsername; }
}