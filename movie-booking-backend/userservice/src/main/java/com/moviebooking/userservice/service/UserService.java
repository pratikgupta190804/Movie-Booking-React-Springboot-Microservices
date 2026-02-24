package com.moviebooking.userservice.service;

import com.moviebooking.userservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse syncUser(UserSyncRequest request);

    UserResponse getUserById(String id);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(String id, UserUpdateRequest request, String requestingUserId, String requestingUserRoles);

    UserResponse assignRole(String id, AssignRoleDTO request, String requestingUserRoles);

    UserResponse updateStatus(String id, StatusUpdateRequest request, String requestingUserRoles);

    void deleteUser(String id, String requestingUserId, String requestingUserRoles);

    Page<UserResponse> getAllUsers(Pageable pageable, String requestingUserRoles);
}
