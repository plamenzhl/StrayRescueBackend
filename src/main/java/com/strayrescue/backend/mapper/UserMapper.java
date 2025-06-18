package com.strayrescue.backend.mapper;

import com.strayrescue.backend.dto.response.UserDto;
import com.strayrescue.backend.dto.response.UserSummaryDto;
import com.strayrescue.backend.dto.response.CurrentUserDto;
import com.strayrescue.backend.model.User;
import com.strayrescue.backend.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Component
public class UserMapper {
    
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        // NO PASSWORD HASH - NEVER EXPOSE IT!
        
        return dto;
    }
    
    public UserSummaryDto toSummaryDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserSummaryDto(
            user.getId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName()
        );
    }
    
    public CurrentUserDto toCurrentUserDto(User user) {
        if (user == null) {
            return null;
        }
        
        CurrentUserDto dto = new CurrentUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        // Add role-based permissions
        dto.setPermissions(getPermissionsForRole(user.getRole()));
        
        return dto;
    }
    
    public List<UserDto> toDtoList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<UserSummaryDto> toSummaryDtoList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }
    
    private List<String> getPermissionsForRole(UserRole role) {
        if (role == null) {
            return Arrays.asList("read:public");
        }
        
        switch (role) {
            case ADMIN:
                return Arrays.asList(
                    "read:all", "write:all", "delete:all", 
                    "manage:users", "manage:animals", "manage:images"
                );
            case MODERATOR:
                return Arrays.asList(
                    "read:all", "write:animals", "write:images", 
                    "moderate:content"
                );
            case USER:
                return Arrays.asList(
                    "read:public", "write:own", "upload:images"
                );
            default:
                return Arrays.asList("read:public");
        }
    }
}