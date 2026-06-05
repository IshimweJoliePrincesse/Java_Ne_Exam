package com.utility.billing.controller;

import com.utility.billing.dto.AuthDtos.RoleUpdateRequest;
import com.utility.billing.dto.UserResponse;
import com.utility.billing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "2. User Management", description = "Admin can view, delete, upgrade, and revoke user roles. Customers can view their own account.")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List users", description = "Admin returns all user accounts.")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my account", description = "Authenticated user returns their own account details.")
    public UserResponse findOwnAccount() {
        return userService.findOwnAccount();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Admin returns one user account by integer ID.")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upgrade or change user role", description = "Admin changes a user's role and sends an email notification explaining the role change.")
    public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return userService.updateRole(id, request);
    }

    @PatchMapping("/{id}/role/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revoke user role", description = "Admin revokes an elevated role by returning the user to ROLE_CUSTOMER and sends an email notification.")
    public UserResponse revokeRole(@PathVariable Long id) {
        return userService.revokeRole(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user", description = "Admin permanently deletes a user account by Long.")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
