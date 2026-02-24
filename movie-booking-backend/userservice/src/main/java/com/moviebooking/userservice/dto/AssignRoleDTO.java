package com.moviebooking.userservice.dto;

import com.moviebooking.userservice.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleDTO {

    @NotNull(message = "Role is required")
    private Role role;
}
