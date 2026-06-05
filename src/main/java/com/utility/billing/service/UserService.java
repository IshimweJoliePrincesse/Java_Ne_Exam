package com.utility.billing.service;

import com.utility.billing.dto.AuthDtos.RoleUpdateRequest;
import com.utility.billing.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse findOwnAccount();
    UserResponse updateRole(Long id, RoleUpdateRequest request);
    UserResponse revokeRole(Long id);
    void delete(Long id);
}
