package com.utility.billing.service;

import com.utility.billing.dto.CustomerDtos.*;

import java.util.List;

public interface CustomerService {
    List<CustomerResponse> findAll(String search);
    CustomerResponse findById(Long id);
    CustomerResponse findOwnProfile();
    CustomerResponse update(Long id, CustomerRequest request);
    CustomerResponse updateOwnProfile(CustomerProfileUpdateRequest request);
    CustomerResponse updateStatus(Long id, StatusRequest request);
}
