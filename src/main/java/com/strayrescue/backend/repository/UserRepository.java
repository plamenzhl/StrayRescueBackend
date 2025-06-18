package com.strayrescue.backend.repository;

import com.strayrescue.backend.model.User;
import com.strayrescue.backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find users by role
    List<User> findByRole(UserRole role);
    
    // Find users by first and last name
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, String lastName);
    
    // Custom query to find active users (if you add an 'active' field later)
    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.createdAt DESC")
    List<User> findByRoleOrderByCreatedAtDesc(UserRole role);
}