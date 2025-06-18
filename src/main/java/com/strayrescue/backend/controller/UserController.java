package com.strayrescue.backend.controller;

import com.strayrescue.backend.dto.response.UserDto;
import com.strayrescue.backend.mapper.UserMapper;
import com.strayrescue.backend.model.User;
import com.strayrescue.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber()
            );
            // Return DTO instead of raw entity
            UserDto userDto = userMapper.toDto(user);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = userMapper.toDtoList(users);
        return ResponseEntity.ok(userDtos);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> ResponseEntity.ok(userMapper.toDto(u)))
                  .orElse(ResponseEntity.notFound().build());
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, 
                                       @RequestBody UserUpdateRequest request) {
        try {
            User updatedUser = userService.updateUser(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber()
            );
            // Return DTO instead of raw entity
            UserDto userDto = userMapper.toDto(updatedUser);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Inner classes for request bodies
    public static class UserRegistrationRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phoneNumber;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    public static class UserUpdateRequest {
        private String firstName;
        private String lastName;
        private String phoneNumber;

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
}