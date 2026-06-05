package com.utility.billing.service.impl;

import com.utility.billing.dto.AuthDtos.RoleUpdateRequest;
import com.utility.billing.dto.UserResponse;
import com.utility.billing.entity.AppUser;
import com.utility.billing.enums.Role;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.UserMapper;
import com.utility.billing.repository.AppUserRepository;
import com.utility.billing.service.EmailService;
import com.utility.billing.service.UserService;
import com.utility.billing.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final AppUserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(get(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findOwnAccount() {
        return userMapper.toResponse(SecurityUtils.currentUser());
    }

    @Override
    public UserResponse updateRole(Long id, RoleUpdateRequest request) {
        AppUser user = get(id);
        Role oldRole = user.getRole();
        user.setRole(request.role());
        emailService.sendRoleChangedEmail(user.getEmail(), user.getFullName(),
                "Your role has been upgraded/changed from " + oldRole + " to " + request.role() + ".");
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse revokeRole(Long id) {
        AppUser user = get(id);
        Role oldRole = user.getRole();
        user.setRole(Role.ROLE_CUSTOMER);
        emailService.sendRoleChangedEmail(user.getEmail(), user.getFullName(),
                "Your previous role " + oldRole + " has been revoked. Your account now has ROLE_CUSTOMER access.");
        return userMapper.toResponse(user);
    }

    @Override
    public void delete(Long id) {
        AppUser user = get(id);
        userRepository.delete(user);
    }

    private AppUser get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
