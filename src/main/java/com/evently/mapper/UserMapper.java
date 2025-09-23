package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.evently.model.User;
import com.evently.dto.user.UserDTO;
import com.evently.dto.user.UserRegistrationDTO;
import com.evently.dto.user.UserUpdateDTO;

/**
 * MapStruct mapper for User entity and DTOs.
 * Handles conversion between User entity and various User DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    /**
     * Convert User entity to UserDTO.
     * Maps fullName using custom method.
     */
    @Mapping(target = "fullName", source = "user", qualifiedByName = "getFullName")
    UserDTO toUserDTO(User user);
    
    /**
     * Convert list of User entities to list of UserDTOs.
     */
    List<UserDTO> toUserDTOList(List<User> users);
    
    /**
     * Convert UserRegistrationDTO to User entity.
     * Excludes computed fields and sets defaults.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "dateJoined", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "password", ignore = true) // Password will be encoded separately
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toUser(UserRegistrationDTO registrationDTO);
    
    /**
     * Update User entity from UserUpdateDTO.
     * Only updates modifiable fields.
     */
    // Update method for patching existing user
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "dateJoined", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateUserFromDTO(UserUpdateDTO updateDTO, @MappingTarget User user);
    
    /**
     * Custom method to get full name from User entity.
     */
    @Named("getFullName")
    default String getFullName(User user) {
        return user.getFullName();
    }
}