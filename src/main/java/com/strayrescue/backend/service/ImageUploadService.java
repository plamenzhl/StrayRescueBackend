package com.strayrescue.backend.service;

import com.strayrescue.backend.model.Animal;
import com.strayrescue.backend.model.AnimalImage;
import com.strayrescue.backend.model.User;
import com.strayrescue.backend.repository.AnimalImageRepository;
import com.strayrescue.backend.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Arrays;

@Service
public class ImageUploadService {

    @Autowired
    private AnimalImageRepository animalImageRepository;
    
    @Autowired
    private AnimalRepository animalRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.region:eu-north-1}")
    private String region;
    
    @Value("${aws.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aws.secret-access-key:}")
    private String secretAccessKey;

    // Image size configurations
    private static final int LARGE_SIZE = 1200;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    private S3Client getS3Client() {
        // Use default credential chain if no explicit credentials provided
        // This will check: environment variables, IAM roles, credential files, etc.
        if (accessKeyId != null && secretAccessKey != null && 
            !accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();
        } else {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    /**
     * Upload multiple images for an animal
     */
    public List<AnimalImage> uploadImagesForAnimal(Long animalId, MultipartFile[] files, User uploadedBy) {
        // Validate animal exists
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + animalId));

        return Arrays.stream(files)
                .map(file -> uploadSingleImage(animal, file, uploadedBy))
                .toList();
    }

    /**
     * Upload a single image with processing
     */
    public AnimalImage uploadSingleImage(Animal animal, MultipartFile file, User uploadedBy) {
        try {
            // Validate file
            validateFile(file);
            
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Process and resize image
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            byte[] processedImageData = processImage(originalImage, LARGE_SIZE);
            
            // Upload to S3
            String s3Key = "animals/" + animal.getId() + "/" + uniqueFileName;
            String s3Url = uploadToS3(s3Key, processedImageData, file.getContentType());
            
            // Create and save AnimalImage record
            AnimalImage animalImage = new AnimalImage();
            animalImage.setFileName(originalFileName);
            animalImage.setS3Key(s3Key);
            animalImage.setS3Url(s3Url);
            animalImage.setFileSize((long) processedImageData.length);
            animalImage.setContentType(file.getContentType());
            animalImage.setAnimal(animal);
            animalImage.setUploadedBy(uploadedBy);
            
            // Set as primary if this is the first image
            long imageCount = animalImageRepository.countByAnimalId(animal.getId());
            animalImage.setIsPrimary(imageCount == 0);
            animalImage.setDisplayOrder((int) imageCount);
            
            return animalImageRepository.save(animalImage);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image: " + e.getMessage(), e);
        }
    }

    /**
     * Set primary image for an animal
     */
    public AnimalImage setPrimaryImage(Long animalId, Long imageId) {
        // Reset all images for this animal to non-primary
        List<AnimalImage> animalImages = animalImageRepository.findByAnimalIdOrderByDisplayOrderAsc(animalId);
        animalImages.forEach(img -> img.setIsPrimary(false));
        animalImageRepository.saveAll(animalImages);
        
        // Set the specified image as primary
        AnimalImage primaryImage = animalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        primaryImage.setIsPrimary(true);
        
        return animalImageRepository.save(primaryImage);
    }

    /**
     * Delete an image
     */
    public void deleteImage(Long imageId) {
        AnimalImage image = animalImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        // Delete from S3
        deleteFromS3(image.getS3Key());
        
        // Delete from database
        animalImageRepository.delete(image);
        
        // If this was the primary image, set another one as primary
        if (image.getIsPrimary()) {
            Optional<AnimalImage> nextImage = animalImageRepository
                    .findByAnimalIdOrderByDisplayOrderAsc(image.getAnimal().getId())
                    .stream()
                    .findFirst();
            
            nextImage.ifPresent(img -> {
                img.setIsPrimary(true);
                animalImageRepository.save(img);
            });
        }
    }

    /**
     * Get all images for an animal
     */
    public List<AnimalImage> getImagesForAnimal(Long animalId) {
        return animalImageRepository.findByAnimalIdOrderByDisplayOrderAsc(animalId);
    }

    /**
     * Get primary image for an animal
     */
    public Optional<AnimalImage> getPrimaryImageForAnimal(Long animalId) {
        return animalImageRepository.findByAnimalIdAndIsPrimaryTrue(animalId);
    }

    // PRIVATE HELPER METHODS

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum limit of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
        
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("File type not allowed. Supported types: " + ALLOWED_TYPES);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg"; // Default extension
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private byte[] processImage(BufferedImage originalImage, int maxSize) throws IOException {
        // Calculate new dimensions while maintaining aspect ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth, newHeight;
        
        if (originalWidth > originalHeight) {
            newWidth = Math.min(maxSize, originalWidth);
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            newHeight = Math.min(maxSize, originalHeight);
            newWidth = (int) (newHeight * aspectRatio);
        }
        
        // Create resized image
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        
        // Improve image quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }

    private String uploadToS3(String key, byte[] data, String contentType) {
        S3Client s3Client = getS3Client();
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength((long) data.length)
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
        
        // Return the S3 URL
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    private void deleteFromS3(String key) {
        S3Client s3Client = getS3Client();
        
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        
        s3Client.deleteObject(deleteObjectRequest);
    }
}