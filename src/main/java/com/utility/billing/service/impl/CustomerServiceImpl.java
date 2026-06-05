package com.utility.billing.service.impl;

import com.utility.billing.dto.CustomerDtos.*;
import com.utility.billing.entity.Customer;
import com.utility.billing.enums.Role;
import com.utility.billing.enums.Status;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.CustomerMapper;
import com.utility.billing.repository.AppUserRepository;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.repository.MeterRepository;
import com.utility.billing.service.CustomerService;
import com.utility.billing.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AppUserRepository userRepository;
    private final MeterRepository meterRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll(String search) {
        List<Customer> customers;
        if (search == null || search.isBlank()) {
            customers = customerRepository.findAll();
        } else {
            String term = "%" + search.toLowerCase() + "%";
            Specification<Customer> spec = (root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("fullName")), term),
                    cb.like(cb.lower(root.get("nationalId")), term),
                    cb.like(cb.lower(root.get("phoneNumber")), term));
            customers = customerRepository.findAll(spec);
        }
        return customers.stream()
                .filter(this::belongsToCurrentCustomerRole)
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return customerMapper.toResponse(get(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findOwnProfile() {
        return customerMapper.toResponse(getByCurrentUserEmail());
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = get(id);
        customer.setFullName(request.fullName());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateOwnProfile(CustomerProfileUpdateRequest request) {
        Customer customer = getByCurrentUserEmail();
        customer.setFullName(request.fullName());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setAddress(request.address());
        userRepository.findByEmail(customer.getEmail()).ifPresent(user -> {
            user.setFullName(request.fullName());
            user.setPhoneNumber(request.phoneNumber());
        });
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateStatus(Long id, StatusRequest request) {
        Customer customer = get(id);
        customer.setStatus(request.status());
        userRepository.findByEmail(customer.getEmail()).ifPresent(user -> user.setStatus(request.status()));
        if (request.status() == Status.INACTIVE) {
            meterRepository.findByCustomerId(customer.getId())
                    .forEach(meter -> meter.setStatus(Status.INACTIVE));
        }
        return customerMapper.toResponse(customer);
    }

    private Customer get(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private Customer getByCurrentUserEmail() {
        String email = SecurityUtils.currentUser().getEmail();
        return customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
    }

    private boolean belongsToCurrentCustomerRole(Customer customer) {
        return userRepository.findByEmail(customer.getEmail())
                .map(user -> user.getRole() == Role.ROLE_CUSTOMER)
                .orElse(false);
    }
}
