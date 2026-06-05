package com.utility.billing.mapper;

import com.utility.billing.dto.UserResponse;
import com.utility.billing.entity.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(AppUser user);
}
