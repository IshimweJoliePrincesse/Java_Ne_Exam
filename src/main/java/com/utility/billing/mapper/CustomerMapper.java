package com.utility.billing.mapper;

import com.utility.billing.dto.CustomerDtos.CustomerResponse;
import com.utility.billing.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
}
