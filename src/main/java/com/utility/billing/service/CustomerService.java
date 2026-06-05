package com.utility.billing.service;

import com.utility.billing.dto.CustomerDtos.*;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);
    List<CustomerResponse> findAll(String search);
    CustomerResponse findById(UUID id);
    CustomerResponse findOwnProfile();
    CustomerResponse update(UUID id, CustomerRequest request);
    CustomerResponse updateOwnProfile(CustomerProfileUpdateRequest request);
    CustomerResponse updateStatus(UUID id, StatusRequest request);
}
