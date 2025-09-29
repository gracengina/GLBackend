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

@Mapper(componentModel = "spring")
public interface UserMapper {
   
    @Mapping(target = "fullName", source = "user", qualifiedByName = "getFullName")
    UserDTO toUserDTO(User user);
    
    
    List<UserDTO> toUserDTOList(List<User> users);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "dateJoined", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "password", ignore = true) 
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toUser(UserRegistrationDTO registrationDTO);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "dateJoined", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateUserFromDTO(UserUpdateDTO updateDTO, @MappingTarget User user);
    
    
    @Named("getFullName")
    default String getFullName(User user) {
        return user.getFullName();
    }
}
