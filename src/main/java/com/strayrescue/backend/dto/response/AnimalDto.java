package com.strayrescue.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AnimalDto {
    private Long id;
    private String name;
    private String species;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String locationDescription;
    private String status;
    private LocalDateTime reportedAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User references without exposing passwords
    private UserSummaryDto reportedBy;
    private UserSummaryDto currentCaretaker;
    
    // Image references
    private List<AnimalImageDto> images;
    private String primaryImageUrl;
    
    public AnimalDto() {}
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    
    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }
    
    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public UserSummaryDto getReportedBy() { return reportedBy; }
    public void setReportedBy(UserSummaryDto reportedBy) { this.reportedBy = reportedBy; }
    
    public UserSummaryDto getCurrentCaretaker() { return currentCaretaker; }
    public void setCurrentCaretaker(UserSummaryDto currentCaretaker) { this.currentCaretaker = currentCaretaker; }
    
    public List<AnimalImageDto> getImages() { return images; }
    public void setImages(List<AnimalImageDto> images) { this.images = images; }
    
    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
}