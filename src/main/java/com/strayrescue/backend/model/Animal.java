package com.strayrescue.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "animals")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Animal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @NotBlank
    @Size(max = 50)
    private String species; // "Dog", "Cat", "Bird", etc.
    
    @Size(max = 50)
    private String breed;
    
    @Enumerated(EnumType.STRING)
    private AnimalStatus status = AnimalStatus.REPORTED;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "estimated_age")
    private String estimatedAge; // "Puppy", "Adult", "Senior", "Unknown"
    
    @Size(max = 20)
    private String gender; // "Male", "Female", "Unknown"
    
    @Size(max = 20)
    private String size; // "Small", "Medium", "Large"
    
    // Location where found
    @Column(name = "location_description")
    private String locationDescription;
    
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;
    
    // Health information
    @Column(name = "health_status")
    private String healthStatus;
    
    @Column(name = "is_vaccinated")
    private Boolean isVaccinated = false;
    
    @Column(name = "is_spayed_neutered")
    private Boolean isSpayedNeutered = false;
    
    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;
    
    // Contact information
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_user_id")
    @NotNull
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "reportedAnimals", "careAnimals", "uploadedImages"})
    private User reportedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_caretaker_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "reportedAnimals", "careAnimals", "uploadedImages"})
    private User currentCaretaker;
    
    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("animal-images")
    private Set<AnimalImage> images;
    
    // Timestamps
    @Column(name = "date_found")
    private LocalDateTime dateFound;
    
    @Column(name = "date_reported")
    private LocalDateTime dateReported = LocalDateTime.now();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;
    
    // Constructors
    public Animal() {}
    
    public Animal(String name, String species, String description, User reportedBy) {
        this.name = name;
        this.species = species;
        this.description = description;
        this.reportedBy = reportedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    
    public AnimalStatus getStatus() { return status; }
    public void setStatus(AnimalStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEstimatedAge() { return estimatedAge; }
    public void setEstimatedAge(String estimatedAge) { this.estimatedAge = estimatedAge; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
    
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    
    public Boolean getIsVaccinated() { return isVaccinated; }
    public void setIsVaccinated(Boolean isVaccinated) { this.isVaccinated = isVaccinated; }
    
    public Boolean getIsSpayedNeutered() { return isSpayedNeutered; }
    public void setIsSpayedNeutered(Boolean isSpayedNeutered) { this.isSpayedNeutered = isSpayedNeutered; }
    
    public String getMedicalNotes() { return medicalNotes; }
    public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }
    
    public User getCurrentCaretaker() { return currentCaretaker; }
    public void setCurrentCaretaker(User currentCaretaker) { this.currentCaretaker = currentCaretaker; }
    
    public Set<AnimalImage> getImages() { return images; }
    public void setImages(Set<AnimalImage> images) { this.images = images; }
    
    public LocalDateTime getDateFound() { return dateFound; }
    public void setDateFound(LocalDateTime dateFound) { this.dateFound = dateFound; }
    
    public LocalDateTime getDateReported() { return dateReported; }
    public void setDateReported(LocalDateTime dateReported) { this.dateReported = dateReported; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}