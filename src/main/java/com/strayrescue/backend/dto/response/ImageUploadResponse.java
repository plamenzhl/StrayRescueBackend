package com.strayrescue.backend.dto.response;

import java.util.List;

public class ImageUploadResponse {
    private String message;
    private int uploadedCount;
    private List<AnimalImageDto> images;  // CHANGED TO USE DTOs
    
    public ImageUploadResponse() {}
    
    public ImageUploadResponse(String message, int uploadedCount, List<AnimalImageDto> images) {
        this.message = message;
        this.uploadedCount = uploadedCount;
        this.images = images;
    }
    
    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public int getUploadedCount() { return uploadedCount; }
    public void setUploadedCount(int uploadedCount) { this.uploadedCount = uploadedCount; }
    
    public List<AnimalImageDto> getImages() { return images; }
    public void setImages(List<AnimalImageDto> images) { this.images = images; }
}