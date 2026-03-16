package com.moviebooking.userservice.service;

import com.moviebooking.userservice.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

    UserResponse syncUser(SyncUserRequest syncUserRequest);

    UserResponse getCurrentMe(String loggedInUser);

    UserResponse getUserById(String requestedUserId);

    Page<UserResponse> getAllUser(Pageable pageable);

    UserResponse updateProfile(UserUpdateDto updateDto, String loggedInUser);

    void disableProfile(String userId, String loggedInUser);
}
